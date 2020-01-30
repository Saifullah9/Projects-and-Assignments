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

X_test = pickle.load(open( "test_images.pkl", "rb" ) )
X_test=(X_test.astype(float))
X_test = X_test.reshape(X_test.shape[0], 64, 64, 1)
model1 = keras.models.load_model('Model1.h5')
model2 = keras.models.load_model('Model2.h5')
model3 = keras.models.load_model('Model3.h5')
model4 = keras.models.load_model('Model4.h5')
model5 = keras.models.load_model('Model5.h5')

prob1 = model1.predict(X_test)
prob2 = model2.predict(X_test)
prob3 = model3.predict(X_test)
prob4 = model4.predict(X_test)
prob5 = model5.predict(X_test)

final_prob_test = prob1+prob2+prob3+prob4+prob5
y_pred_test = (final_prob_test.argmax(axis=1)).astype(int)

np.savetxt("test_submission.csv", np.dstack((np.arange(0, y_pred_test.size),y_pred_test))[0],"%d,%i",header="Id,Category")
