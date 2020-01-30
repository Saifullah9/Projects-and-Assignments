from functools import reduce
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
from itertools import combinations
from copy import deepcopy
from scipy import stats
from keras.utils import plot_model

X_train = pickle.load(open( "X_train.pkl", "rb" ) )
X_test = pickle.load(open( "X_test.pkl", "rb" ) )
y_train = pickle.load(open( "y_train.pkl", "rb" ) )
y_test = pickle.load(open( "y_test.pkl", "rb" ) )

model1 = keras.models.load_model('Model1.h5')
model2 = keras.models.load_model('Model2.h5')
model3 = keras.models.load_model('Model3.h5')
model4 = keras.models.load_model('Model4.h5')
model5 = keras.models.load_model('Model5.h5')

prob1_t = model1.predict(X_test)
prob2_t = model2.predict(X_test)
prob3_t = model3.predict(X_test)
prob4_t = model4.predict(X_test)
prob5_t = model5.predict(X_test)

y_test_ = y_test.argmax(axis=1)

s = [(1,prob1_t),(2,prob2_t),(3,prob3_t),(4,prob4_t),(5,prob5_t)]
for r in range(1,len(s)+1):
    for models in list(combinations([(1,prob1_t),(2,prob2_t),(3,prob3_t),(4,prob4_t),(5,prob5_t)],r)):
        print("Average commitee")
        final_prob_test = deepcopy(models[0][1])
        print(models[0][0])
        for i in range(1,len(models)):
            print(models[i][0])
            final_prob_test = final_prob_test + models[i][1]

        y_pred_2 = (final_prob_test.argmax(axis=1))
        print(float(np.nonzero(y_test_-y_pred_2)[0].size)/float(6000))

        print("Majority Voting")
        final_prob_test = np.reshape(models[0][1].argmax(axis=1), (-1,1))
        print(models[0][0])
        for i in range(1,len(models)):
            print(models[i][0])
            final_prob_test = np.concatenate((final_prob_test,np.reshape(models[i][1].argmax(axis=1), (-1, 1))), axis=1)

        y_pred_2 = stats.mode(final_prob_test, axis=1)[0].reshape(-1)
        print(float(np.nonzero(y_test_-y_pred_2)[0].size)/float(6000))

        print("Median")
        final_prob_test = np.reshape(models[0][1], models[0][1].shape + (1,))
        print(models[0][0])
        for i in range(1,len(models)):
            print(models[i][0])
            final_prob_test = np.concatenate((final_prob_test,np.reshape(models[i][1], models[i][1].shape + (1,) )), axis=2)

        y_pred_2 = np.median(final_prob_test, axis=2).reshape(6000,10).argmax(axis=1)
        print(y_pred_2.shape)
        print(float(np.nonzero(y_test_-y_pred_2)[0].size)/float(6000))

prob1_t = model1.predict(X_train)
prob2_t = model2.predict(X_train)
prob3_t = model3.predict(X_train)
prob4_t = model4.predict(X_train)
prob5_t = model5.predict(X_train)

y_test_ = y_train.argmax(axis=1)

s = [(1,prob1_t),(2,prob2_t),(3,prob3_t),(4,prob4_t),(5,prob5_t)]
for r in range(1,len(s)+1):
    for models in list(combinations([(1,prob1_t),(2,prob2_t),(3,prob3_t),(4,prob4_t),(5,prob5_t)],r)):
        print("Average commitee")
        final_prob_test = deepcopy(models[0][1])
        print(models[0][0])
        for i in range(1,len(models)):
            print(models[i][0])
            final_prob_test = final_prob_test + models[i][1]

        y_pred_2 = (final_prob_test.argmax(axis=1))
        print(float(np.nonzero(y_test_-y_pred_2)[0].size)/float(34000))

        print("Majority Voting")
        final_prob_test = np.reshape(models[0][1].argmax(axis=1), (-1,1))
        print(models[0][0])
        for i in range(1,len(models)):
            print(models[i][0])
            final_prob_test = np.concatenate((final_prob_test,np.reshape(models[i][1].argmax(axis=1), (-1, 1))), axis=1)

        y_pred_2 = stats.mode(final_prob_test, axis=1)[0].reshape(-1)
        print(float(np.nonzero(y_test_-y_pred_2)[0].size)/float(34000))

        print("Median")
        final_prob_test = np.reshape(models[0][1], models[0][1].shape + (1,))
        print(models[0][0])
        for i in range(1,len(models)):
            print(models[i][0])
            final_prob_test = np.concatenate((final_prob_test,np.reshape(models[i][1], models[i][1].shape + (1,) )), axis=2)

        y_pred_2 = np.median(final_prob_test, axis=2).reshape(34000,10).argmax(axis=1)
        print(y_pred_2.shape)
        print(float(np.nonzero(y_test_-y_pred_2)[0].size)/float(34000))

#Evaluate the Models:
score = model1.evaluate(X_test, y_test, verbose=1)
print("1")
print('Test accuracy:', score[1])

score = model2.evaluate(X_test, y_test, verbose=1)
print("2")
print('Test accuracy:', score[1])

score = model3.evaluate(X_test, y_test, verbose=1)
print("3")
print('Test accuracy:', score[1])

score = model4.evaluate(X_test, y_test, verbose=1)
print("4")
print('Test accuracy:', score[1])

score = model5.evaluate(X_test, y_test, verbose=1)
print("5")
print('Test accuracy:', score[1])


score = model1.evaluate(X_train, y_train, verbose=1)
print("1")
print('training accuracy:', score[1])

score = model2.evaluate(X_train, y_train, verbose=1)
print("2")
print('training accuracy:', score[1])

score = model3.evaluate(X_train, y_train, verbose=1)
print("3")
print('training accuracy:', score[1])

score = model4.evaluate(X_train, y_train, verbose=1)
print("4")
print('training accuracy:', score[1])

score = model5.evaluate(X_train, y_train, verbose=1)
print("5")
print('training accuracy:', score[1])

#print Model picture
plot_model(model1, show_shapes=True, show_layer_names=False, to_file='model1.png')
plot_model(model2, show_shapes=True, show_layer_names=False, to_file='model2.png')
plot_model(model3, show_shapes=True, show_layer_names=False, to_file='model3.png')
plot_model(model4, show_shapes=True, show_layer_names=False, to_file='model4.png')
plot_model(model5, show_shapes=True, show_layer_names=False, to_file='model5.png')
