# from keras.applications.resnet50 import ResNet50, preprocess_inputls
import os
os.environ['KMP_DUPLICATE_LIB_OK']='True'
from keras.applications.mobilenet_v2 import MobileNetV2, preprocess_input
model = MobileNetV2(include_top=False, weights='imagenet',input_shape=(224,224,3))

for layer in model.layers:
    layer.trainable = True

from keras.layers import Dense, Dropout, GlobalAveragePooling2D
x = model.output
x = GlobalAveragePooling2D()(x)
x = Dropout(0.3)(x)

from keras.models import Model
#dense : 출력 class를 줄인다!
predictions = Dense(7, activation= 'softmax')(x)
model = Model(inputs = model.input, output = predictions)
print(model.summary())

from keras.preprocessing.image import ImageDataGenerator
train_datagen = ImageDataGenerator(
    rotation_range=35,
    width_shift_range=0.2,
    height_shift_range=0.2,
    preprocessing_function=preprocess_input,
    fill_mode='constant'
)

#directory = '/Users/kimwanki/developer/testcase/data/training'
#directory 별로 만들겠다.

train_generator = train_datagen.flow_from_directory(
    directory = '/home/opensw1/keras/train/',
    target_size = (224, 224),
    batch_size = 10,
    #one hot으로 만들어줌
    class_mode = 'categorical',
    #binary - > 인풋값 그대로.
    shuffle = True
)


val_datagen = ImageDataGenerator(
    preprocessing_function = preprocess_input
)


#directory 별로 만들겠다.
val_generator = val_datagen.flow_from_directory(
    directory = '/home/opensw1/keras/val/',
    target_size = (224, 224),
    batch_size = 10,
    #one hot으로 만들어줌
    class_mode = 'categorical',
    #binary - > 인풋값 그대로.
    shuffle=False
)
import math
# import keras
# keras.losses.categorical_crossentropy()
from keras import optimizers

# TODO : 저장된 모델의 경로를 불러와서 모델을 그대로 사용가능.

# model.load_weights('/home/opensw04/model.h5')
model.compile(loss="categorical_crossentropy", optimizer=optimizers.Adam(lr=0.0001),metrics=["accuracy"])
from keras.callbacks import ModelCheckpoint

# ckpt_filepath :
checkpoint = ModelCheckpoint('/home/opensw1/keras/model.h5', save_best_only=True,
                             monitor='val_accuracy',verbose=1, save_weights_only=False, mode='auto', period = 1)

callback_list = [checkpoint]
result = model.fit_generator(train_generator, steps_per_epoch= math.ceil(150/ 10),
                             epochs=100,
                             callbacks= callback_list,
                             validation_data=val_generator,
                             validation_steps= 20,
                             shuffle=True
                             )











