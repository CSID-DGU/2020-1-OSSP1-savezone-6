import numpy as np
import tensorflow as tf
import os
import random
import cv2
import imutils
import math
import matplotlib.pyplot as plt


def vgg(input_model, batch_bool):
    # TODO : initializer 마다 초기값을 조금 다르게 설정한다.
    # he initializer 가중치 초기화 -> relu
    kernel_init = tf.variance_scaling_initializer(scale=2.0)


    # xavier initializer -> sigmoid()
    # kernel_init = tf.contrib.layers.xavier_initializer()
    # tf.constant(3)
    # (10,56,56,3)

    filter_count = 30
    # input = tf.placeholder(dtype=tf.float32,shape=(10,56,56,3))
    # batch_bool = tf.placeholder(dtype=tf.bool)

    # 정규
    layer = input_model / 255.0
    # print(layer.shape)
    layer = tf.layers.conv2d(inputs=layer, filters=filter_count, kernel_size=(3, 3), strides=(1, 1), padding='same',
                             kernel_initializer=kernel_init)

    # print(layer.shape)
    # filter : 특성을 잡아낸다. feature map()
    # padding : input output 크기를 같게 하기 위해 padding을 준다.
    # 배치 정규화 >> 성능 차이가 크다.
    # training -> 곱셈을 할수록 정보가 날라간다

    layer = tf.layers.batch_normalization(layer, training=batch_bool)
    layer = tf.nn.relu(layer)
    # print(layer.shape)
    # TODO : 논문 정보에 따른 학습 순서 지정? 2* conv -> 한 레이어에서 conv를 두행 번 진
    layer = tf.layers.conv2d(inputs=layer, filters=filter_count, kernel_size=(3, 3), strides=(1, 1), padding='same',
                             kernel_initializer=kernel_init)
    layer = tf.layers.batch_normalization(layer)
    layer = tf.nn.relu(layer)
    # TODO : conv_layer ->  batch -> relu : 이 성능이 더 좋았다. 데이터 마다 다름.

    # TODO : 이미지를 줄인다. max pooling 잘 안씀, average 또는 strides 값을 변경.
    layer = tf.layers.max_pooling2d(layer, (2, 2), (2, 2), padding='same')
    print(layer.shape)

    layer = tf.layers.conv2d(inputs=layer, filters=filter_count * 2, kernel_size=(3, 3), strides=(1, 1), padding='same',
                             kernel_initializer=kernel_init)
    # strides => 1, 1
    # filter : 특성을 잡아낸다. feature map() # padding : input output 크기를 같게 하기 위해 padding을 준다.
    # 배치 정규화 >> 성능 차이가 크다.
    layer = tf.layers.batch_normalization(layer, training=batch_bool)
    layer = tf.nn.relu(layer)

    # TODO : conv_layer -> batch -> relu : 이 성능이 더 좋았다. 데이터 마다 다름.
    # TODO : 이미지를 줄인다. max pooling 잘 안씀, average 또는 strides 값을 변경.
    layer = tf.layers.max_pooling2d(layer, (2, 2), (2, 2), padding='same')
    print(layer.shape)

    layer = tf.layers.conv2d(inputs=layer, filters=filter_count * (2 ** 2), kernel_size=(3, 3), strides=(1, 1),
                             padding='same',
                             kernel_initializer=kernel_init)
    layer = tf.layers.batch_normalization(layer, training=batch_bool)
    layer = tf.nn.relu(layer)
    layer = tf.layers.max_pooling2d(layer, (2, 2), (2, 2), padding='same')
    layer = tf.layers.conv2d(inputs=layer, filters=filter_count * (2 ** 3), kernel_size=(3, 3), strides=(1, 1),
                             padding='same',
                             kernel_initializer=kernel_init)
    layer = tf.layers.batch_normalization(layer, training=batch_bool)
    layer = tf.nn.relu(layer)
    print(layer.shape)

    layer = tf.layers.max_pooling2d(layer, (2, 2), (2, 2), padding='same')
    layer = tf.keras.layers.Flatten()(layer)
    # 인풋을 뒤에 작성

    net = tf.keras.layers.Dense(2048, kernel_initializer=kernel_init)(layer)
    net = tf.keras.layers.Dense(1024, kernel_initializer=kernel_init)(net)
    net = tf.keras.layers.Dense(3, kernel_initializer=kernel_init)(net)
    # softmax : 확류 합이 1이 되도록 sclae
    return net #마지막 컨볼루션

def img_loader(image_list,train):
    batch_img_list = []
    for i in image_list:
        input = cv2.imread(i)

        if(train == 1):
            flip_ran = random.randint(0, 3)
            if flip_ran == 0:
                input = cv2.flip(input, 0)
            elif flip_ran == 1:
                input = cv2.flip(input, 1)
            elif flip_ran == 2:
                input = cv2.flip(input, 1)
                input = cv2.flip(input, 0)

            rotate_v = random.randint(-30, 30)
            shift_y = input.shape[0] * 0.1
            shift_y = math.ceil(shift_y)
            random_y = random.randint(-shift_y, shift_y)
            shift_x = input.shape[1] * 0.1
            shift_x = math.ceil(shift_x)
            random_x = random.randint(-shift_x, shift_x)

            input = imutils.translate(input, random_x, random_y)

            input = imutils.rotate(input, rotate_v)


        input = cv2.resize(input, (56, 56))

        # hyper parameter >> 어떤 데이터에서 어떤 모델은 어떨때 가장 좋은지를 계속 피드백.

        batch_img_list.append(input)

    # dtype - int 로 하면 error 발생된다.
    return np.array(batch_img_list, dtype=np.float32)

def next_batch(data_list, mini_batch_size, next_cnt):
    cnt = mini_batch_size * next_cnt
    batch_list = data_list[cnt:cnt + mini_batch_size]
    return batch_list

# 이미지 전체를 불러와서 일부를 넣는다.
# 폴더 안에 있는 모든 사진 파일을 불러온다.
# file_path = "/Users/kimwanki/Downloads/example/"
# folder_name = ["A", "B", "C"]
file_path = "/Users/kimwanki/developer/testcase/"
folder_name = ["sweetpo", "straw", "potato"]

test_path = "/Users/kimwanki/developer/testcase/test/"
image_list = []
label_list = []

t_image_list = []
t_label_list = []

index = 0

for i in folder_name:
    image_path = file_path + i
    _image_list = [file_path + i + '/' + j for j in os.listdir(image_path)]
    _label_list = [index for j in range(len(_image_list))]
    image_list += _image_list
    label_list += _label_list
    index += 1

index = 0
for i in folder_name:
    image_path = test_path + i
    _image_list = [test_path + i +'/' + j for j in os.listdir(image_path)]
    _label_list = [index for j in range(len(_image_list))]
    t_image_list += _image_list
    t_label_list += _label_list
    index +=1
print(t_image_list)
print(len(t_image_list))
temp = t_image_list.index('/Users/kimwanki/developer/testcase/test/sweetpo/.DS_Store')

t_image_list.pop(temp)
t_label_list.pop(temp)
print(t_image_list)


# print("shuffle : seed(random)")
# print(image_list)


random_variable = random.randint
test_variable = random.randint
# 학습 순서를 섞는다.
random.seed(random_variable)
random.shuffle(image_list)
random.seed(random_variable)
random.shuffle(label_list)

random.seed(test_variable)
random.shuffle(t_image_list)
random.seed(test_variable)
random.shuffle(t_label_list)

# test = cv2.imread(image_list[0],0)


# image show // imshow(parameter) -> show()
# plt.imshow(test)
# plt.show()


batch_size = 10
batch_cnt = math.ceil(len(image_list) / batch_size)
test_batch_cnt = math.ceil(len(t_image_list)/ batch_size)
x_data = tf.placeholder(dtype=tf.float32, shape=(None, 56, 56, 3))
y_data = tf.placeholder(dtype=tf.float32, shape=(None, 3))

train_bool = tf.placeholder(dtype=tf.bool)

model = vgg(x_data, train_bool)
print('model.shape', model.shape)
print('y_data.shape', y_data.shape)

# global_step = tf.Variable(0, trainable=False, name='global_step')
prob = tf.nn.softmax(model)

loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y_data, logits=model))

#배치 정규화를 위한 선언
update_ops = tf.get_collection(tf.GraphKeys.UPDATE_OPS)

with tf.control_dependencies(update_ops):
    optimizer = tf.train.AdamOptimizer(learning_rate=0.00001).minimize(loss)

init = tf.global_variables_initializer()

with tf.Session() as sess:
    sess.run(init)
    # saver = tf.train.Saver(tf.global_variables())

    # ckpt = tf.train.get_checkpoint_state('./model')
    saver = tf.train.Saver()
    saver.restore(sess = sess, save_path='./model/cnn.ckpt')

    # if ckpt and tf.train.checkpoint_exists(ckpt.model_checkpoint_path):
    #     saver.restore(sess, ckpt.model_checkpoint_path)
    # else:

    # model-> Pre train -> model

    epoch = 30
    for one_epoch in range(epoch):
        print('epoch : ',one_epoch)
        val_accuracy = 0
        train_accuracy = 0
        max = 0
        for i in range(batch_cnt):
            x_one_batch = next_batch(image_list, batch_size, i)
            # print('one_batach_X_list', x_one_batch)
            x_one_batch = img_loader(x_one_batch,1)
            y_one_batch = next_batch(label_list, batch_size, i)
            # ex) [0 , 1, 1, ,1, 1,  ] -> one_hot encoding
            y_one_batch = np.eye(3)[y_one_batch]
            # [len(set(label_list))]
            _, batch_loss, batch_prob = sess.run([optimizer,loss, prob],
                                              feed_dict={x_data: x_one_batch, y_data: y_one_batch, train_bool: True})

            # batch_prob,batch_loss  = sess.run([optimizer, loss],feed_dict={x_data: x_one_batch, y_data: y_one_batch, train_bool: True})

            result = np.argmax(batch_prob,axis = 1)
            label = np.argmax(y_one_batch,axis = 1)
            # print("predict_Y",result)
            # print("True_Y", label)

            one_batch_train_acc = np.mean(np.equal(result, label).astype(int))
            # print("result", one_epoch, ':', one_batch_train_acc)
            train_accuracy += one_batch_train_acc
        train_accuracy = train_accuracy/batch_cnt
        print("train_accuracy : ",train_accuracy)

        for i in range(test_batch_cnt):
            x_one_batch = next_batch(t_image_list, batch_size, i)
            # print('one_batach_X_list', x_one_batch)
            x_one_batch = img_loader(x_one_batch, 0)
            y_one_batch = next_batch(t_label_list, batch_size, i)
            # ex) [0 , 1, 1, ,1, 1,  ] -> one_hot encoding
            y_one_batch = np.eye(3)[y_one_batch]
            batch_loss, batch_prob = sess.run([loss, prob],
                                                 feed_dict={x_data: x_one_batch, y_data: y_one_batch, train_bool: False})

            result = np.argmax(batch_prob, axis=1)
            label = np.argmax(y_one_batch, axis=1)

            one_batch_val_acc = np.mean(np.equal(result, label).astype(int))
            val_accuracy += one_batch_val_acc
        val_accuracy = val_accuracy/batch_cnt

        print("test_accuaacy : ", val_accuracy)
        if (val_accuracy > max):
            max=val_accuracy
            saver.save(sess, './model/cnn.ckpt')
