#!flask/bin/python
# -*- coding: utf-8 -*-  
import os
import random
import time
from flask import Flask, jsonify, abort, make_response,request, send_from_directory
from pydub import AudioSegment

app = Flask(__name__)

basedir = os.path.abspath(os.path.dirname(__file__))
tasks = [
    {
        'id': 1,
        'title': u'Buy groceries',
        'description': u'Milk, Cheese, Pizza, Fruit, Tylenol',
        'done': False
    },
    {
        'id': 2,
        'title': u'危险',
        'description': u'Need to find a good Python tutorial on the web',
        'done': False
    }
]

@app.route('/todo/api/v1.0/tasks', methods=['GET'])
def get_tasks():
    return jsonify({'tasks': tasks})

@app.route('/todo/api/v1.0/tasks/<int:task_id>', methods=['GET'])
def get_task(task_id):
    task = filter(lambda t: t['id'] == task_id, tasks)
    if len(task) == 0:
        abort(404)
    return jsonify({'task': task[0]})

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

@app.route('/todo/api/v1.0/tasks', methods=['POST'])
def create_task():
    if not request.json or not 'title' in request.json:
        abort(400)
    task = {
        'id': tasks[-1]['id'] + 1,
        'title': request.json['title'],
        'description': request.json.get('description', ""),
        'done': False
    }
    tasks.append(task)
    return jsonify({'task': task}), 201


ALLOWED_EXTENSIONS = set(['txt','mp3','amr','3gp','jpg','PNG','xlsx','gif','GIF'])
# 用于判断文件后缀
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.',1)[1] in ALLOWED_EXTENSIONS


# 上传文件接口
@app.route('/uploadfiles', methods=['POST'])
def upload():
    # single file
    # file = request.files['file']
    # print f.filename

    # mulitfile
    files = request.files.getlist('file')
    print files
    #filenames = request.form.getlist("fileName")
    #print filenames
    file_dir = os.path.join(basedir,'upload_folder')
    if not os.path.exists(file_dir):
        os.makedirs(file_dir)
    for index in range(len(files)):
        f = files[index]
        print f.filename
        #fileName = filenames[index]
        if f and allowed_file(f.filename):
        #    fname = secure_filename(f.filename)
            f.save(os.path.join(file_dir,f.filename))
        else:
            return jsonify({'msg':'upload failed!','code':1000,'data':'no'}), 201
    return jsonify({'msg':'upload success!','code':200,'data':'yes'}), 201

def TimeStampToTime(timestamp):
	timeStruct = time.localtime(timestamp)
	return time.strftime('%Y/%m/%d',timeStruct)

p = ["胎心音",  "肺音",  "儿童语音", "其他音"]
# 获取音频接口
@app.route('/getVoiceRecords', methods=['GET'])
def getVoiceRecord():
    babyRecords = []
    httpResList = []
    httpResponse = []
    begin = int(request.args.get('start'))
    count = int(request.args.get('count'))
    file_dir = os.path.join(basedir,'upload_folder')
    lists = os.listdir(file_dir)
    lists.sort()
    start=begin
    end = 0
    if((start + count) < len(lists)):
        end = (start+count)
    else:
        end = len(lists)
    while(start < end):
        #print start, end
        f = lists[start]
        #print f
        tmpfile = os.path.join(file_dir, f)
        sound = AudioSegment.from_file(tmpfile, format="amr")
        #print int(len(sound)/1000.0)
        #print sound.duration_seconds
        if os.path.isfile(tmpfile):
            print tmpfile
            record = {
                'name':f,
                'date':TimeStampToTime(os.path.getctime(tmpfile)),
                'duration':int(len(sound)/1000.0),
                'category':random.choice(p) # random select a element from p.
            }
        babyRecords.append(record)
        start = start + 1

    httpRes = {
        'start':begin,
        'count':count,
        'total':len(lists),
        'dataList':babyRecords
    }

    #httpResList.append(httpRes)

    response = {
		'code':200,
		'msg':'success!',
		'data':httpRes
	}
    #httpResponse.append(response)
    return jsonify(response), 201
    #return jsonify({'httpResponse':httpResponse}), 201

# 获取疫苗信息

vaccinfos= [
    {
        'vaccineName':u'麻风疫苗',
        'isFree':True,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':3
    },
    {
        'vaccineName':u'狂犬疫苗',
        'isFree':False,
        'isInjected':True,
        'injectDate':u'2017-02-23',
        'ageToInject':3
    },
    {
        'vaccineName':u'疫苗1',
        'isFree':True,
        'isInjected':True,
        'injectDate':u'2017-03-23',
        'ageToInject':5
    },
    {
        'vaccineName':u'疫苗2',
        'isFree':False,
        'isInjected':True,
        'injectDate':u'2017-05-20',
        'ageToInject':5
    },
    {
        'vaccineName':u'疫苗3',
        'isFree':True,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':4
    },
    {
        'vaccineName':u'疫苗4',
        'isFree':True,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':9
    },
    {
        'vaccineName':u'疫苗5',
        'isFree':True,
        'isInjected':True,
        'injectDate':u'2016-03-23',
        'ageToInject':9
    },
    {
        'vaccineName':u'疫苗6',
        'isFree':True,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':1
    },
    {
        'vaccineName':u'疫苗7',
        'isFree':True,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':2
    },
    {
        'vaccineName':u'疫苗8',
        'isFree':False,
        'isInjected':True,
        'injectDate':u'2017-10-21',
        'ageToInject':8
    },
    {
        'vaccineName':u'疫苗9',
        'isFree':True,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':9
    },
    {
        'vaccineName':u'疫苗10',
        'isFree':False,
        'isInjected':True,
        'injectDate':u'2017-04-10',
        'ageToInject':5
    },
    {
        'vaccineName':u'疫苗11',
        'isFree':False,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':18
    },
    {
        'vaccineName':u'疫苗12',
        'isFree':True,
        'isInjected':False,
        'injectDate':u'',
        'ageToInject':13
    }
]

@app.route('/getVaccineInfo', methods=['GET'])
def getVaccineInfo():

    begin = int(request.args.get('start'))
    count = int(request.args.get('count'))
    vaccinfos.sort()
    start = begin
    end = 0
    if((start + count) < len(vaccinfos)):
        end = (start+count)
    else:
        end = len(vaccinfos)
    httpRes = {
        'start':begin,
        'count':count,
        'total':len(vaccinfos),
        'dataList':vaccinfos[start:end]
    }
    response = {
        'code':200,
        'msg':'success!',
        'data':httpRes
    }
    return jsonify(response), 201

productions= [
    {
        'id':1,
        'name':u'普通B超',
        'week':2,
        'isDone':True
    },
    {
        'id':1,
        'name':u'B超2',
        'week':2,
        'isDone':False
    },
    {
        'id':1,
        'name':u'B超3',
        'week':3,
        'isDone':False
    },
    {
        'id':2,
        'name':u'血常规五项',
        'week':4,
        'isDone':False
    },
    {
        'id':3,
        'name':u'四维彩超',
        'week':3,
        'isDone':False
    },
    {
        'id':2,
        'name':u'B超5',
        'week':4,
        'isDone':False
    },
    {
        'id':3,
        'name':u'B超6',
        'week':4,
        'isDone':True
    },
    {
        'id':4,
        'name':u'B超7',
        'week':3,
        'isDone':True
    },
    {
        'id':6,
        'name':u'普通B超2',
        'week':10,
        'isDone':True
    },
    {
        'id':2,
        'name':u'普通B超3',
        'week':10,
        'isDone':True
    },
    {
        'id':1,
        'name':u'普通B超4',
        'week':10,
        'isDone':False
    },
    {
        'id':4,
        'name':u'普通B超5',
        'week':10,
        'isDone':True
    },
    {
        'id':1,
        'name':u'普通B超6',
        'week':11,
        'isDone':False
    },
    {
        'id':2,
        'name':u'普通B超7',
        'week':11,
        'isDone':True
    },
    {
        'id':3,
        'name':u'普通B超8',
        'week':11,
        'isDone':False
    },
    {
        'id':4,
        'name':u'普通B超9',
        'week':11,
        'isDone':False
    }
]

def week(s):
    return s['week']

@app.route('/getProductionInfo', methods=['GET'])
def getProductionInfo():

    begin = int(request.args.get('start'))
    end = int(request.args.get('end'))
    count = 0
    temp = sorted(productions,  key = week) #按照某个函数返回值排序
    resData= []
    for index in range(len(temp)):
        info = temp[index]
        if info['week'] >= begin and info['week'] <= end:
            resData.append(info)
            count = count + 1
    httpRes = {
        'start':begin,
        'count':count,
        'total':len(temp),
        'dataList':resData
    }
    response = {
        'code':200,
        'msg':'success!',
        'data':httpRes
    }
    return jsonify(response), 201

# 下载文件
dirpath=os.path.join(basedir,'upload_folder')
@app.route("/download/<path:filename>")
def downloader(filename):
    return send_from_directory(dirpath,filename,as_attachment=True)

growup_records=[]
#创建成长记录
@app.route("/growup/create", methods=['POST'])
def create_growup_record():
    if not request.json or not 'date' in request.json:
        abort(400)
    record = {
        'date': request.json.get('date', ""),
        'content': request.json.get('content', ""),
        'description': request.json.get('description', ""),
    }
    growup_records.append(record)
    return jsonify({'record': record}), 201



if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
