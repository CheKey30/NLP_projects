computing environment:
Here are the modules I used in this task:
import nltk
import pandas as pd
from sklearn.model_selection import train_test_split
from nltk.tokenize import word_tokenize
import re
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np
from sklearn.model_selection import cross_val_score
from sklearn.dummy import DummyClassifier
from sklearn.linear_model import LogisticRegression
from scipy.stats import ttest_ind
from sklearn.metrics import accuracy_score
from imblearn.over_sampling import RandomOverSampler
from nltk.stem import PorterStemmer

additional resources:
I used the "glove" as my pre-trained word vectors. Here is the link: https://nlp.stanford.edu/projects/glove/
There is no need to download that, I already put what I used in the folder

How to run the program:
Please use Jupyter note book to open the "NLP_HW3_allinone.ipynb" file.
Then just run the only cell is enough. It takes about 3 minutes and 10 seconds to finish.

Python version:
I used python 3.7.1 to do this assignment.