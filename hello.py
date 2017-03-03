#!flask/bin/python
# -*- coding: utf-8 -*-  
from flask import Flask, jsonify, abort, make_response,request
import os, time, random
from werkzeug import secure_filename
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
        'title': u'Learn Python',
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
    start = int(request.args.get('start'))
    count = int(request.args.get('count'))
    file_dir = os.path.join(basedir,'upload_folder')
    lists = os.listdir(file_dir)
    lists.sort()
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
        'start':start,
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


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
