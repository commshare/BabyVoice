# -*- coding: utf-8 -*-
# 用来重命名资源文件
import os
path = 'E:\\AndroidStudioProjects\\BabyVoice\\app\\src\\main\\res\\mipmap-xxxhdpi'
keyword = '@3x'
for file in os.listdir(path):
    if os.path.isfile(os.path.join(path,file))==True:
        if file.find(keyword) > 0:
            print "old name is ",file
            a = file.partition(keyword)
            newname = (a[0] + a[2]).lower()
            print "new name is", newname
            if os.path.isfile(os.path.join(path,newname)) == True:
                newname = newname+"_1"
            os.rename(os.path.join(path,file),os.path.join(path,newname))
#        print file.split('.')[-1] 
