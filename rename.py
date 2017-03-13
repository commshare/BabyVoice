# -*- coding: utf-8 -*-
# 用来批量重命名资源文件
# 用法示例：python rename.py xxhdpi @3x
import os, platform, sys

#path = 'app/src/main/res/drawable-xxxhdpi'

sysstr = platform.system()
if(sysstr =="Windows"):
    print ("current OS is Windows")
    path = 'app\\src\\main\\res\\drawable-'
else:
    print ("Other System")
    path = 'app/src/main/res/drawable-'
path = path+sys.argv[1]
keyword = sys.argv[2]
for file in os.listdir(path):
    if os.path.isfile(os.path.join(path,file))==True:
        if file.find(keyword) > 0:
            print "old name is ",file
            a = file.partition(keyword)
            newname = (a[0] + a[2]).lower()
            print "new name is", newname
            if os.path.isfile(os.path.join(path,newname)) == True:
                newname = newname+"_1"
            os.rename(os.path.join(path,file),os.path.join(path, newname))
