# -*- coding:utf-8 -*-
import os.path
import cv2
import shutil
from moviepy.editor import *
import argparse
import os
import sys
import numpy as np
from PIL import Image, ImageDraw, ImageFont
import subprocess
import time

import tkinter
import tkinter.messagebox
from PIL import Image, ImageTk, ImageDraw, ImageFont
from time import sleep
from tkinter import IntVar, StringVar


class MyGui:
    def __init__(self, framesdir, annsdir, mark_cls):
        # 创建tkinter主窗口
        self.labelcnt = 0
        self.labelsum = 0
        self.labels = {}
        for d in os.listdir(framesdir):
            for each_frame in os.listdir(framesdir + d):
                self.labels[self.labelsum] = {}
                self.labels[self.labelsum]['framepath'] = framesdir + d + '/' + each_frame
                self.labels[self.labelsum]['annpath'] = annsdir + d + '/' + each_frame[:each_frame.rfind('.')] + '.txt'
                self.labels[self.labelsum]['dropout'] = 0
                self.labelsum += 1
        print("labelsum : " + str(self.labelsum))
        print("the last one :" + self.labels[self.labelsum - 1]['framepath'])
        self.root = tkinter.Tk()
        # 指定主窗口位置与大小
        self.root.geometry('1900x980+10+10')
        # 不允许改变窗口大小
        self.root.resizable(False, False)
        self.X = tkinter.IntVar(value=0)
        self.Y = tkinter.IntVar(value=0)
        self.selectPosition = None
        if mark_cls == "zhongkong":
            self.idtext = {'0': 'norm', '1': 'look at phone', '2': 'sleep'}
        elif mark_cls == "zhaxian":
            self.idtext = {'0': 'person'}
        self.idlist = []
        self.lines = []

        self.outlog = StringVar()
        self.outlog.set("读取成功！")
        self.text_outlog = tkinter.Label(self.root, font="Helvetica 15 bold", textvariable=self.outlog, state='normal',
                                         width=3, bg="#4A708B", fg="#8B1A1A",
                                         disabledforeground="yellow", highlightbackground="black",
                                         highlightcolor="red", highlightthickness=1, bd=0)
        self.text_outlog.place(x=1710, y=50 + 35 * 21, width=180, height=150)

        # canvas尺寸
        screenWidth = 1703  # root.winfo_screenwidth()
        screenHeight = 958  # root.winfo_screenheight()
        # 创建顶级组件容器
        # self.top = tkinter.Toplevel(self.root,width=screenWidth,height=screenHeight)
        # 不显示最大化、最小化按钮
        # self.root.overrideredirect(True)

        self.canvas = tkinter.Canvas(self.root, bg='white', width=screenWidth, height=screenHeight)
        im = Image.open(self.labels[self.labelcnt]['framepath'])
        im = im.resize((1703, 958))
        self.draw_bbox(im)
        self.image = ImageTk.PhotoImage(im)
        self.canvas.create_image(0, 0, anchor='nw', image=self.image)
        self.root.title(self.labels[self.labelcnt]['framepath'])

        def but_preCaptureClick():
            if (self.labelcnt == 0):
                return
            self.labelcnt -= 1
            if (self.labels[self.labelcnt]['dropout']):
                temp = self.labelcnt + 1
                while self.labelcnt >= 0 and self.labels[self.labelcnt]['dropout']:
                    self.labelcnt -= 1
                if (self.labelcnt < 0):
                    self.labelcnt = temp
                    return
            ims = Image.open(self.labels[self.labelcnt]['framepath'])
            ims = ims.resize((1703, 958))
            self.draw_bbox(ims)
            self.image = ImageTk.PhotoImage(ims)
            self.canvas.create_image(0, 0, anchor='nw', image=self.image)
            self.root.title(self.labels[self.labelcnt]['framepath'])
            print("self.labelcnt : " + str(self.labelcnt))

        def but_afterCaptureClick():
            if (self.labelcnt == self.labelsum):
                return

            self.labelcnt += 1
            if (self.labels[self.labelcnt]['dropout']):
                temp = self.labelcnt - 1
                while self.labelcnt < self.labelsum and self.labels[self.labelcnt]['dropout']:
                    self.labelcnt += 1
                if (self.labelcnt == self.labelsum):
                    self.labelcnt = temp
                    return
            ims = Image.open(self.labels[self.labelcnt]['framepath'])
            ims = ims.resize((1703, 958))
            self.draw_bbox(ims)
            self.image = ImageTk.PhotoImage(ims)
            self.canvas.create_image(0, 0, anchor='nw', image=self.image)
            self.root.title(self.labels[self.labelcnt]['framepath'])
            print("self.labelcnt : " + str(self.labelcnt))

        def but_dropoutCaptureClick():
            a = tkinter.messagebox.askokcancel('警告', 'dropout不可复原，确认？')

            if (a is False):
                return

            src = self.labels[self.labelcnt]['framepath']
            name = src[src.rfind('frames') + 6:]
            os.rename(src, 'd:/phone/guitest/wait_frames' + name)

            src = self.labels[self.labelcnt]['annpath']
            name = src[src.rfind('anns') + 4:]
            os.rename(src, 'd:/phone/guitest/wait_anns' + name)
            self.labels[self.labelcnt]['dropout'] = 1
            but_afterCaptureClick()

        self.canvas.bind('<Button-1>', self.onLeftButtonDown)
        self.canvas.bind('<B1-Motion>', self.onLeftButtonMove)
        self.canvas.bind('<ButtonRelease-1>', self.onLeftButtonUp)
        self.canvas.place(x=0, y=0)  # pack(fill=tkinter.Y,expand=tkinter.YES)

        self.but_dropout = tkinter.Button(self.root, text="Drop out", command=but_dropoutCaptureClick)
        self.but_dropout.place(x=1710, y=0, width=80, height=20)
        self.but_pre = tkinter.Button(self.root, text="<- Prev", command=but_preCaptureClick)
        self.but_pre.place(x=1710, y=25, width=80, height=20)
        self.but_after = tkinter.Button(self.root, text="After ->", command=but_afterCaptureClick)
        self.but_after.place(x=1795, y=25, width=80, height=20)

        # 启动消息主循环
        self.root.mainloop()

    # 鼠标左键按下的位置
    def onLeftButtonDown(self, event):
        self.X.set(event.x)
        self.Y.set(event.y)
        # 开始画框的标志
        self.sel = True

    # 鼠标左键移动，显示选取的区域
    def onLeftButtonMove(self, event):
        if not self.sel:
            return
        global lastDraw
        try:
            # 删除刚画完的图形，否则鼠标移动的时候是黑乎乎的一片矩形
            self.canvas.delete(lastDraw)
        except Exception as e:
            pass
        lastDraw = self.canvas.create_rectangle(self.X.get(), self.Y.get(), event.x, event.y, outline='yellow')

    # 获取鼠标左键抬起的位置，记录区域
    def onLeftButtonUp(self, event):
        self.sel = False
        try:
            self.canvas.delete(lastDraw)
        except Exception as e:
            pass
        sleep(0.1)
        print(event.x, event.y)
        upx = event.x if event.x < 1703 else 1703
        upy = event.y if event.y < 958 else 958
        upx = upx if upx > 0 else 0
        upy = upy if upy > 0 else 0
        myleft, myright = sorted([self.X.get(), upx])
        mytop, mybottom = sorted([self.Y.get(), upy])
        self.selectPosition = (myleft, myright, mytop, mybottom)
        print("选择区域bbox：xmin:" + str(self.selectPosition[0]) + ' ymin:' + str(
            self.selectPosition[2]) + ' xmax:' + str(self.selectPosition[1]) + ' ymax:' + str(
            self.selectPosition[3]))
        self.but_addCaptureClick()

    def but_confirmCaptureClick(self, event):
        # 如果修改了文本框的值，把新值写会标注文件对应行
        the_butt = event.widget
        the_butt_name = the_butt._name
        # print(the_butt_name)
        for line_i in range(len(self.lines)):
            # print(n._name)
            if (self.idlist[line_i * 4]._name == the_butt_name):
                new_id = self.idlist[line_i * 4 + 3].get()
                try:
                    new_idn = int(new_id)
                except ValueError:
                    # tkinter.messagebox.showinfo(title='错误', message='必须是0到2的数字')
                    self.outlog.set('Error：0到2的数字')
                    break
                if (new_idn not in range(0, 3)):
                    # tkinter.messagebox.showinfo(title='错误', message='必须是0到2的数字')
                    # self.text_outlog.delete(0, 100)
                    self.outlog.set('Error：0到2的数字')
                    break
                # print(new_id)
                # print(self.lines[line_i])
                newlines = []
                for i in range(len(self.lines)):
                    if (i == line_i):
                        newlines.append(str(new_idn) + self.lines[i][1:])
                    else:
                        newlines.append(self.lines[i])

                with open(self.labels[self.labelcnt]['annpath'], 'w') as annfile:
                    annfile.writelines(newlines)
                self.outlog.set('修改成功！第' + str(line_i + 1) + "行")
                break
        ims = Image.open(self.labels[self.labelcnt]['framepath'])
        ims = ims.resize((1703, 958))
        self.draw_bbox(ims)
        self.image = ImageTk.PhotoImage(ims)
        self.canvas.create_image(0, 0, anchor='nw', image=self.image)
        self.root.title(self.labels[self.labelcnt]['framepath'])

    def but_deleteCaptureClick(self, event):
        # 删除标注文件中对应行
        # top = tkinter.Toplevel()
        # top.title('警告')
        # msg = tkinter.Message(top,text='删除不可复原，确认删除？',width=150)
        # msg.pack()
        a = tkinter.messagebox.askokcancel('警告', '删除不可复原，确认删除？')

        if (a is False):
            return
        else:
            the_butt = event.widget
            the_butt_name = the_butt._name
            for line_i in range(len(self.lines)):
                # print(n._name)
                if (self.idlist[line_i * 4 + 1]._name == the_butt_name):
                    newlines = []
                    for i in range(len(self.lines)):
                        if (i == line_i):
                            continue
                        else:
                            newlines.append(self.lines[i])

                    with open(self.labels[self.labelcnt]['annpath'], 'w') as annfile:
                        annfile.writelines(newlines)
                    self.outlog.set('删除成功！第' + str(line_i + 1) + "行")
                    break
            ims = Image.open(self.labels[self.labelcnt]['framepath'])
            ims = ims.resize((1703, 958))
            self.draw_bbox(ims)
            self.image = ImageTk.PhotoImage(ims)
            self.canvas.create_image(0, 0, anchor='nw', image=self.image)
            self.root.title(self.labels[self.labelcnt]['framepath'])

    def but_addCaptureClick(self):
        # 新添加Label控件和Entry控件以及Button，接收在canvas中点出的框坐标
        self.canvas.create_rectangle(self.selectPosition[0], self.selectPosition[2], self.selectPosition[1],
                                     self.selectPosition[3], outline="red")
        w = self.selectPosition[1] - self.selectPosition[0]
        h = self.selectPosition[3] - self.selectPosition[2]
        x = (self.selectPosition[0] + w / 2) / 1703.0
        y = (self.selectPosition[2] + h / 2) / 958.0
        w = w / 1703.0
        h = h / 958.0
        bbox = ('0', str('%.6f' % x), str('%.6f' % y), str('%.6f' % w), str('%.6f' % h))
        new_line = ' '.join(bbox)
        print(new_line)
        self.lines.append(new_line)
        with open(self.labels[self.labelcnt]['annpath'], 'w') as annfile:
            annfile.writelines(self.lines)
        self.outlog.set('添加成功！请标注\n第' + str(self.num + 1) + '行')
        ims = Image.open(self.labels[self.labelcnt]['framepath'])
        ims = ims.resize((1703, 958))
        self.draw_bbox(ims)
        self.image = ImageTk.PhotoImage(ims)
        self.canvas.create_image(0, 0, anchor='nw', image=self.image)
        self.root.title(self.labels[self.labelcnt]['framepath'])

    def destroy_idbar(self):
        for e in self.idlist:
            e.destroy()
        self.idlist = []
        # self.text_outlog.delete(0, 100)

    def draw_bbox(self, im):
        self.destroy_idbar()
        draw = ImageDraw.Draw(im)
        self.lines = []
        with open(self.labels[self.labelcnt]['annpath'], 'r') as f:
            self.lines = f.readlines()
        if (len(self.lines) > 0 and self.lines[-1] == '\n'):
            self.lines.pop()

        bboxs = []
        ids = []
        for i in range(len(self.lines)):
            if ('\n' not in self.lines[i]):
                self.lines[i] = self.lines[i] + '\n'
            line = self.lines[i].strip().split(' ')
            bboxs.append([line[1], line[2], line[3], line[4]])
            ids.append([line[0]])
        print(self.lines, len(self.lines))
        self.num = 0
        for b, i in zip(bboxs, ids):
            # print(type(b),b[0],b[1])
            self.num += 1
            xmin = int((float(b[0]) - float(b[2]) / 2.) * 1703)
            ymin = int((float(b[1]) - float(b[3]) / 2.) * 958)
            xmax = int((float(b[0]) + float(b[2]) / 2.) * 1703)
            ymax = int((float(b[1]) + float(b[3]) / 2.) * 958)
            draw.rectangle((xmin, ymin, xmax, ymax), outline=(0, 255, 15))
            idt = self.idtext[i[0]]
            font1 = ImageFont.truetype("C:/Windows/Fonts/simsunb.ttf", 24)
            draw.ink = 0 + 0 * 256 + 0 * 256 * 256
            draw.rectangle((xmin, ymin - 30, xmin + 120, ymin), fill=128)
            draw.text((xmin, ymin - 30), str(self.num) + ' ' + idt, font=font1)
            text = tkinter.Label(self.root, text=str(self.num))
            text.place(x=1710, y=50 + 35 * (self.num - 1), width=20, height=30)
            e = tkinter.Entry(self.root)
            e.insert(0, i[0])
            e.place(x=1731, y=50 + 35 * (self.num - 1), width=30, height=30)
            but_confirm = tkinter.Button(self.root, text="确认")
            but_confirm.place(x=1780, y=50 + 35 * (self.num - 1), width=50, height=30)
            but_confirm.bind('<Button-1>', self.but_confirmCaptureClick)
            # but_confirm.bind('<ButtonRelease-1>',self.but_confirmrelease)
            but_delete = tkinter.Button(self.root, text="删除")
            but_delete.place(x=1831, y=50 + 35 * (self.num - 1), width=50, height=30)
            but_delete.bind('<Button-1>', self.but_deleteCaptureClick)

            self.idlist.append(but_confirm)
            self.idlist.append(but_delete)
            self.idlist.append(text)
            self.idlist.append(e)
            # but_add = tkinter.Button(self.root, text="添加", command=self.but_addCaptureClick)
            # but_add.place(x=1780, y=50 + 35 * (self.num), width=50, height=30)
            # self.idlist.append(but_add)


def mvtxt(txtdir):
    cnt = 0
    c = 0
    cc = 0

    for t in os.listdir(txtdir):
        if os.path.isfile(txtdir + t):
            cnt += 1
            if t[t.rfind('_'):][1] == 'b':
                c += 1
                shutil.copyfile(txtdir + t, txtdir + t[:t.rfind('_')] + '/' + t)
            else:
                cc += 1
                shutil.copyfile(txtdir + t, txtdir + t[:t.rfind('.')] + '/' + t)


def ch_time(txtdir):
    for tn in os.listdir(txtdir):
        if os.path.isdir(txtdir + tn):
            lines = []
            with open(txtdir + tn + '/' + tn + '.txt', 'r') as f:
                lines = f.readlines()

            newlines = []
            for line in lines:
                t = line.strip().split(' ')
                s = int(t[0]) / 25

                line = str(s)
                s = int(t[1]) / 25

                line = line + ' ' + str(s) + '\n'
                newlines.append(line)

            with open(txtdir + tn + '/' + tn + '_changetime.txt', 'w') as f:
                f.writelines(newlines)


def cut(videodir, chtdir):
    cnt = 0

    flag = 0

    for vn in os.listdir(videodir):
        name = vn[:vn.rfind('.')]
        for ds in os.listdir(chtdir + name + '/'):
            if ds[:ds.rfind('_')] == 'cuted':
                flag = 1
                break

        if flag == 1:
            cnt += 1
            flag = 0
            continue

        clip = VideoFileClip(videodir + vn)
        lines = []
        with open(chtdir + name + '/' + name + '_changetime.txt', 'r') as f:
            lines = f.readlines()

        i = 0
        for line in lines:
            i += 1
            line = line.strip().split(' ')
            s = int(line[0])
            e = int(line[1])
            c = clip.subclip(s, e)

            c.to_videofile(chtdir + name + "/cuted_" + str(i) + ".mp4", fps=25, remove_temp=False)
        clip.reader.close()

        cnt += 1
        # 下面将地址和属性写进数据库


def findbadvideo(videodir, outdir):
    sum = 0
    for d in os.listdir(videodir):
        if ('7号' in d):
            for each_video in os.listdir(videodir + d):
                if ('mp4' in each_video):
                    filename = videodir + d + '/' + each_video
                    # filename = "d:/phone/python/7号高炉中控室_1E7ABC08_1531197647_52.mp4"
                    result = subprocess.Popen(["D:/搜狗高速下载/ffmpeg-4.0.1-win64-static/bin/ffprobe", filename],
                                              stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
                    for x in result.stdout.readlines():
                        x = str(x, 'utf-8')
                        if ("Duration" in x):
                            # print(type(x),x,x[4])
                            t = x.split(',')
                            m = t[0].split(':')[1:][1]
                            s = t[0].split(':')[1:][2].split('.')[0]
                            if (int(m) * 60 + int(s) > 1095):
                                sum += 1
                                shutil.copyfile(filename, outdir + filename.split('/')[3])


def repairvideo(badvideodir, outdir, liwai):
    fps = 25
    for each_video in os.listdir(badvideodir):
        name = each_video[each_video.find('_'):each_video.rfind('.')]
        if name == liwai:
            print("生成视频中 : " + outdir + each_video)
            fourcc = cv2.VideoWriter_fourcc('D', 'I', 'V', 'X')
            video_writer = cv2.VideoWriter(filename=outdir + name + '.mp4', fourcc=fourcc, fps=fps,
                                           frameSize=(1280, 720))
            im_names = os.listdir(outdir + name)
            for im_name in range(len(im_names)):
                if os.path.exists(outdir + name + '/' + name + '_' + str(im_name) + '.jpg'):
                    img = cv2.imread(filename=outdir + name + '/' + name + '_' + str(im_name) + '.jpg')
                    # print(im_name)
                    img = cv2.resize(img, (1280, 720), interpolation=cv2.INTER_CUBIC)
                    video_writer.write(img)

            print(outdir + name + '/' + name + '_' + str(im_name) + '.jpg' + ' done!')
            video_writer.release()
            cv2.waitKey(10)
            try:
                os.system("rd/s/q " + outdir + name)
            except Exception as e:
                pass
                print(e)
            continue

        vc = cv2.VideoCapture(badvideodir + each_video)
        print(badvideodir + each_video)
        c = 1
        if vc.isOpened():
            rval, frame = vc.read()
        else:
            rval = False
        while rval:
            # print('1')
            rval, frame = vc.read()
            # cv2.imshow("sdf",frame)
            # rows, cols, channel = frame.shape
            # if((c) == 0):
            # print(outdir + name+'/'+name + '_'+str(int(c)) + '.jpg')
            cv2.imwrite(outdir + name + '/' + name + '_' + str(int(c)) + '.jpg', frame)
            c = c + 1
            # cv2.waitKey(1)
        vc.release()

        fourcc = cv2.VideoWriter_fourcc('D', 'I', 'V', 'X')
        video_writer = cv2.VideoWriter(filename=outdir + name + '.mp4', fourcc=fourcc, fps=fps,
                                       frameSize=(1280, 720))
        im_names = os.listdir(outdir + name)
        for im_name in range(len(im_names)):
            if os.path.exists(outdir + name + '/' + name + '_' + str(im_name) + '.jpg'):
                img = cv2.imread(filename=outdir + name + '/' + name + '_' + str(im_name) + '.jpg')
                # print(im_name)
                img = cv2.resize(img, (1280, 720), interpolation=cv2.INTER_CUBIC)
                video_writer.write(img)
                print(outdir + name + '/' + name + '_' + str(im_name) + '.jpg' + ' done!')

        video_writer.release()
        cv2.waitKey(10)
        try:
            os.system("rd/s/q " + outdir + name)
        except Exception as e:
            pass
            print(e)


def cap_video(videodir, outdir, exepath, cap_name):
    videoname = []
    for file in os.listdir(videodir):
        if cap_name in file:
            for mp4 in os.listdir(videodir + file):
                index = mp4.rfind('.')
                videoname.append([mp4[:index], file])

    cnt = 0
    for name, file in videoname:
        try:
            os.makedirs(outdir + name)
        except FileExistsError:
            print("已存在：" + outdir + name)
            continue
        except Exception as e:
            print(e)
            exit(0)
        cmd = "start /min " + exepath + " " + outdir + name + " cap_video " + videodir + file + "/" + name + ".mp4 600"
        print(cmd)
        cnt += 1

        if cnt < 20:
            os.system(cmd)
        else:
            cmd = "start /min /wait " + exepath + " " + outdir + name + " cap_video " + videodir + file + "/" + name + ".mp4 600"
            print(cmd)
            os.system(cmd)
            cnt = 0
            time.sleep(60)


def change_key(framesdir, chkey):
    if chkey == '7号高炉中控室':
        key = '7'
    elif chkey == '大棒线粗轧轧机区':
        key = 'da'
    for d in os.listdir(framesdir):
        newname = d.replace(chkey, key)
        os.rename(framesdir + d, framesdir + newname)
        for f in os.listdir(framesdir + newname):
            n = f.replace(chkey, key)
            os.rename(framesdir + newname + '/' + f, framesdir + newname + '/' + n)


def main(argv):
    parser = argparse.ArgumentParser()
    parser.add_argument('-cls', default='cut', help='cls of interface')
    parser.add_argument('-txtdir', default='\\path\\to\\txtdir',
                        help='path to txtdir\n')
    parser.add_argument('-videodir', default='\\path\\to\\videodir',
                        help='path to videodir\n')
    parser.add_argument('-repair_out', default='\\path\\to\\repairoutdir',
                        help='path to after repair dir')
    parser.add_argument('-repair_except', default='', help='except not to repair')
    parser.add_argument('-cap_out', default='', help='cap_video frame out dir')
    parser.add_argument('-cap_exe', default='', help='cap_frames tool exe path')
    parser.add_argument('-cap_name', default='', help='first name of mp4')
    parser.add_argument('-framesdir', default='', help='path to frames dir\n')
    parser.add_argument('-annsdir', default='', type=str,
                        help='Output annotations directory\n')
    parser.add_argument('-mark_cls', default='', help='mark cls: zhongkong zhaxian\n')
    parser.add_argument('-key', default='', help='key of mp4 name\n')

    args = parser.parse_args()
    if (args.cls == "cut"):
        mvtxt(args.txtdir)
        ch_time(args.txtdir)
        # 删除原txt文件
        cut(args.videodir, args.txtdir)
    elif (args.cls == "repair"):
        findbadvideo(args.videodir, args.repair_out)
        repairvideo(args.repair_out, args.videodir)
    elif (args.cls == "cap_video"):
        cap_video(args.videodir, args.cap_out, args.cap_exe, args.cap_name)
    elif (args.cls == "mark"):
        w = MyGui(args.framesdir, args.annsdir, args.mark_cls)
    elif (args.cls == "chkey"):
        change_key(args.framesdir, args.key)


if __name__ == '__main__':
    main(sys.argv)

