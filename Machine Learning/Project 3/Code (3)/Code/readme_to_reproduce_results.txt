To reproduce the results from the procedure:
	Run BaseCNNandFiveCNN.py with the data files found in this folder
	Results: 5 saved models to then use for Ensemble and training and validation accuracy
	
	Run IndividualMdoelResultsAndEnsembleResults.py with the data files found in ValidationAndTrainingData
	Results: Training and Validation Accuracy of all Ensemble Method with all Subsets of Models
		 and Training and Validation Accuracy of 5 Models.


To reproduce the results from Kaggle:
	Run BaseCNNandFiveCNN.py with the the complete training set given (40000 data points) and  make sure to edit the code by removing anything to do with validation set
	Result: 5 saved models

	Run EnsembleForKagglePredictions 
	Result: CSV file containg the predictions



Imports:
keras
numpy
tensorflow
pickle
pandas
sklearn
itertools
scipy
functools
