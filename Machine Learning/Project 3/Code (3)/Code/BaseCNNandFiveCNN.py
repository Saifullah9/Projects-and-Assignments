import keras
from keras.models import Sequential, Model
from keras.layers import Dense, Activation, Conv2D,MaxPooling2D,Dropout,Flatten, Input
from sklearn.model_selection import train_test_split
from keras.layers.normalization import BatchNormalization
import pickle
import pandas as pd # to read csv and handle dataframe
import numpy as np
import tensorflow as tf
from keras import backend as K
from keras.regularizers import l2
from keras.models import load_model
from keras.callbacks import ReduceLROnPlateau

#cnn0 refers to base CNN
def cnn0(inp,num_category):
    conv1=Conv2D(32, kernel_size=(3, 3),padding='same',activation='relu')(inp)
    maxp1=MaxPooling2D(pool_size=(2, 2))(conv1)

    conv2=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp1)
    maxp2=MaxPooling2D(pool_size=(2, 2))(conv2)

    conv3=Conv2D(128, kernel_size=(3, 3),padding='same',activation='relu')(maxp2)
    maxp3=MaxPooling2D(pool_size=(2, 2))(conv3)
    drpf3=(Dropout(0.35))(maxp3)

    conv4=Conv2D(256, kernel_size=(3, 3),padding='same',activation='relu')(drpf3)
    maxp4=MaxPooling2D(pool_size=(2, 2))(conv4)

    conv5=Conv2D(256, kernel_size=(3, 3),padding='same',activation='relu')(maxp4)
    maxp5=MaxPooling2D(pool_size=(2, 2))(conv5)
    drpf5=(Dropout(0.35))(maxp5)

    flt=(Flatten())(drpf5)
    dnse=(Dense(256, activation='relu'))(flt)
    drpf=(Dropout(0.5))(dnse)
    op=(Dense(10, activation='softmax'))(drpf)

    model=Model(input=inp, output=op)
    model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer=keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False),
                      metrics=['accuracy'])

    return model

def cnn1(inp,num_category):
    conv1=Conv2D(32, kernel_size=(3, 3),padding='same',activation='relu')(inp)
    btch1=BatchNormalization(momentum=0.1,scale=False)(conv1)
    maxp1=MaxPooling2D(pool_size=(2, 2))(btch1)

    conv2=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp1)
    maxp2=MaxPooling2D(pool_size=(2, 2))(conv2)
    drpf2=(Dropout(0.35))(maxp2)

    conv3=Conv2D(128, kernel_size=(3, 3),padding='same',activation='relu')(drpf2)
    maxp3=MaxPooling2D(pool_size=(2, 2))(conv3)
    drpf3=(Dropout(0.5))(maxp3)

    conv4=Conv2D(256, kernel_size=(3, 3),padding='same',activation='relu')(drpf3)
    btch4=BatchNormalization(momentum=0.1,scale=False)(conv4)
    maxp4=MaxPooling2D(pool_size=(2, 2))(btch4)

    conv5=Conv2D(256, kernel_size=(3, 3),padding='same',activation='relu')(maxp4)
    maxp5=MaxPooling2D(pool_size=(2, 2))(conv5)
    drpf5=(Dropout(0.5))(maxp5)

    flt=(Flatten())(drpf5)
    dnse=(Dense(256, activation='relu'))(flt)
    drpf=(Dropout(0.5))(dnse)
    op=(Dense(10, activation='softmax'))(drpf)

    model=Model(input=inp, output=op)
    model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer=keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False),
                      metrics=['accuracy'])

    return model

def cnn2(inp,num_category):
    conv1=Conv2D(32, kernel_size=(3, 3),padding='same',activation='relu')(inp)
    btch1=BatchNormalization(momentum=0.10,scale=False)(conv1)
    maxp1=MaxPooling2D(pool_size=(2, 2))(btch1)

    conv2=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp1)
    maxp2=MaxPooling2D(pool_size=(2, 2))(conv2)
    drpf2=(Dropout(0.25))(maxp2)

    conv3=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(drpf2)
    maxp3=MaxPooling2D(pool_size=(2, 2))(conv3)
    drpf3=(Dropout(0.40))(maxp3)

    conv4=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(drpf3)
    btch4=BatchNormalization(momentum=0.10,scale=False)(conv4)
    maxp4=MaxPooling2D(pool_size=(2, 2))(btch4)

    conv5=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp4)
    maxp5=MaxPooling2D(pool_size=(2, 2))(conv5)


    flt=(Flatten())(maxp5)
    dnse=(Dense(64, activation='relu'))(flt)
    drpf=(Dropout(0.5))(dnse)
    op=(Dense(10, activation='softmax'))(drpf)

    model=Model(input=inp, output=op)
    model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer=keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False),
                      metrics=['accuracy'])

    return model

def cnn3(inp,num_category):
    conv1=Conv2D(32, kernel_size=(3, 3),padding='same',activation='relu')(inp)
    btch1=BatchNormalization(momentum=0.10,scale=False)(conv1)
    maxp1=MaxPooling2D(pool_size=(2, 2))(btch1)

    conv2=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp1)
    maxp2=MaxPooling2D(pool_size=(2, 2))(conv2)
    drpf2=(Dropout(0.25))(maxp2)

    conv3=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(drpf2)
    maxp3=MaxPooling2D(pool_size=(2, 2))(conv3)
    drpf3=(Dropout(0.50))(maxp3)

    conv4=Conv2D(128, kernel_size=(3, 3),padding='same',activation='relu')(drpf3)
    btch4=BatchNormalization(momentum=0.10,scale=False)(conv4)
    maxp4=MaxPooling2D(pool_size=(2, 2))(btch4)

    conv5=Conv2D(128, kernel_size=(3, 3),padding='same',activation='relu')(maxp4)
    maxp5=MaxPooling2D(pool_size=(2, 2))(conv5)
    drpf5=(Dropout(0.5))(maxp5)

    flt=(Flatten())(drpf5)
    dnse=(Dense(128, activation='relu'))(flt)
    drpf=(Dropout(0.5))(dnse)
    op=(Dense(10, activation='softmax'))(drpf)

    model=Model(input=inp, output=op)
    model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer=keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False),
                      metrics=['accuracy'])

    return model

def cnn4(inp,num_category):
    conv1=Conv2D(32, kernel_size=(3, 3),padding='same',activation='relu')(inp)
    btch1=BatchNormalization(momentum=0.10,scale=False)(conv1)
    maxp1=MaxPooling2D(pool_size=(2, 2))(btch1)

    conv2=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp1)
    maxp2=MaxPooling2D(pool_size=(2, 2))(conv2)
    drpf2=(Dropout(0.25))(maxp2)

    conv3=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(drpf2)
    maxp3=MaxPooling2D(pool_size=(2, 2))(conv3)
    drpf3=(Dropout(0.4))(maxp3)

    conv4=Conv2D(128, kernel_size=(3, 3),padding='same',activation='relu')(drpf3)
    btch4=BatchNormalization(momentum=0.10,scale=False)(conv4)
    maxp4=MaxPooling2D(pool_size=(2, 2))(btch4)

    conv5=Conv2D(128, kernel_size=(3, 3),padding='same',activation='relu')(maxp4)
    maxp5=MaxPooling2D(pool_size=(2, 2))(conv5)
    drpf5=(Dropout(0.3))(maxp5)

    conv6=Conv2D(128, kernel_size=(3, 3),padding='same',activation='relu')(drpf5)
    maxp6=MaxPooling2D(pool_size=(2, 2))(conv6)
    drpf6=(Dropout(0.5))(maxp6)

    flt=(Flatten())(drpf6)
    dnse=(Dense(128, activation='relu'))(flt)
    drpf=(Dropout(0.5))(dnse)
    op=(Dense(10, activation='softmax'))(drpf)

    model=Model(input=inp, output=op)
    model.compile(loss=keras.losses.categorical_crossentropy,
                  optimizer=keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False),
                  metrics=['accuracy'])

    return model

def cnn5(inp,num_category):
    conv1=Conv2D(32, kernel_size=(3, 3),padding='same',activation='relu')(inp)
    btch1=BatchNormalization(momentum=0.10,scale=False)(conv1)
    maxp1=MaxPooling2D(pool_size=(2, 2))(btch1)

    conv2=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp1)
    maxp2=MaxPooling2D(pool_size=(2, 2))(conv2)
    drpf2=(Dropout(0.25))(maxp2)

    conv3=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(drpf2)
    maxp3=MaxPooling2D(pool_size=(2, 2))(conv3)
    drpf3=(Dropout(0.40))(maxp3)

    conv4=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(drpf3)
    btch4=BatchNormalization(momentum=0.10,scale=False)(conv4)
    maxp4=MaxPooling2D(pool_size=(2, 2))(btch4)

    conv5=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(maxp4)
    maxp5=MaxPooling2D(pool_size=(2, 2))(conv5)
    drpf5=(Dropout(0.25))(maxp5)

    conv6=Conv2D(64, kernel_size=(3, 3),padding='same',activation='relu')(drpf5)
    maxp6=MaxPooling2D(pool_size=(2, 2))(conv6)

    flt=(Flatten())(maxp6)
    dnse=(Dense(64, activation='relu'))(flt)
    drpf=(Dropout(0.5))(dnse)
    op=(Dense(10, activation='softmax'))(drpf)

    model=Model(input=inp, output=op)
    model.compile(loss=keras.losses.categorical_crossentropy,
                  optimizer=keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False),
                  metrics=['accuracy'])

    return model

X_train = pickle.load(open( "X_train.pkl", "rb" ) )
X_test = pickle.load(open( "X_test.pkl", "rb" ) )
y_train = pickle.load(open( "y_train.pkl", "rb" ) )
y_test = pickle.load(open( "y_test.pkl", "rb" ) )
inp = Input(shape=X_train.shape[1:])

model0=cnn0(inp,10)
batch_size = 512
num_epoch = 400
tf.keras.backend.set_learning_phase(1)
model_log = model0.fit(X_train, y_train,
      batch_size=batch_size,
      epochs=num_epoch,
      verbose=1,
      validation_data=(X_test,y_test))

score = model0.evaluate(X_test, y_test, verbose=1)
print('Test loss:', score[0])
print('Test accuracy:', score[1])
score = model0.evaluate(X_train, y_train, verbose=1)
print('Train loss:', score[0])
print('Train accuracy:', score[1])
y_pred = model0.predict(X_test)
y_pred_2 = y_pred.argmax(axis=1)
y_test_ = y_test.argmax(axis=1)
print(np.nonzero(y_test_-y_pred_2))



model1=cnn1(inp,10)
batch_size = 512
num_epoch = 400
tf.keras.backend.set_learning_phase(1)
model_log = model1.fit(X_train, y_train,
      batch_size=batch_size,
      epochs=num_epoch,
      verbose=1,
      validation_data=(X_test,y_test))

score = model1.evaluate(X_test, y_test, verbose=1)
print('Test loss:', score[0])
print('Test accuracy:', score[1])
score = model1.evaluate(X_train, y_train, verbose=1)
print('Train loss:', score[0])
print('Train accuracy:', score[1])
y_pred = model1.predict(X_test)
y_pred_2 = y_pred.argmax(axis=1)
y_test_ = y_test.argmax(axis=1)
print(np.nonzero(y_test_-y_pred_2))

model2=cnn2(inp,10)
batch_size = 512
num_epoch = 400
tf.keras.backend.set_learning_phase(1)
model_log = model2.fit(X_train, y_train,
      batch_size=batch_size,
      epochs=num_epoch,
      verbose=1,
      validation_data=(X_test,y_test))

score = model2.evaluate(X_test, y_test, verbose=1)
print('Test loss:', score[0])
print('Test accuracy:', score[1])
score = model2.evaluate(X_train, y_train, verbose=1)
print('Train loss:', score[0])
print('Train accuracy:', score[1])
y_pred = model2.predict(X_test)
y_pred_2 = y_pred.argmax(axis=1)
y_test_ = y_test.argmax(axis=1)
print(np.nonzero(y_test_-y_pred_2))

model3=cnn3(inp,10)
batch_size = 512
num_epoch = 400
tf.keras.backend.set_learning_phase(1)
model_log = model3.fit(X_train, y_train,
      batch_size=batch_size,
      epochs=num_epoch,
      verbose=1,
      validation_data=(X_test,y_test))

score = model3.evaluate(X_test, y_test, verbose=1)
print('Test loss:', score[0])
print('Test accuracy:', score[1])
score = model3.evaluate(X_train, y_train, verbose=1)
print('Train loss:', score[0])
print('Train accuracy:', score[1])
y_pred = model3.predict(X_test)
y_pred_2 = y_pred.argmax(axis=1)
y_test_ = y_test.argmax(axis=1)
print(np.nonzero(y_test_-y_pred_2))

model4=cnn4(inp,10)
batch_size = 512
num_epoch = 400
tf.keras.backend.set_learning_phase(1)
model_log = model4.fit(X_train, y_train,
      batch_size=batch_size,
      epochs=num_epoch,
      verbose=1,
      validation_data=(X_test,y_test))

score = model4.evaluate(X_test, y_test, verbose=1)
print('Test loss:', score[0])
print('Test accuracy:', score[1])
score = model4.evaluate(X_train, y_train, verbose=1)
print('Train loss:', score[0])
print('Train accuracy:', score[1])
y_pred = model4.predict(X_test)
y_pred_2 = y_pred.argmax(axis=1)
y_test_ = y_test.argmax(axis=1)
print(np.nonzero(y_test_-y_pred_2))

model5=cnn5(inp,10)
batch_size = 512
num_epoch = 400
tf.keras.backend.set_learning_phase(1)
model_log = model5.fit(X_train, y_train,
      batch_size=batch_size,
      epochs=num_epoch,
      verbose=1,
      validation_data=(X_test,y_test))

score = model5.evaluate(X_test, y_test, verbose=1)
print('Test loss:', score[0])
print('Test accuracy:', score[1])
score = model5.evaluate(X_train, y_train, verbose=1)
print('Train loss:', score[0])
print('Train accuracy:', score[1])
y_pred = model5.predict(X_test)
y_pred_2 = y_pred.argmax(axis=1)
y_test_ = y_test.argmax(axis=1)
print(np.nonzero(y_test_-y_pred_2))

model1.save('Model1.h5')
model2.save('Model2.h5')
model3.save('Model3.h5')
model4.save('Model4.h5')
model5.save('Model5.h5')

