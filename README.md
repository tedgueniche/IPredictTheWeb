# IPredictTheWeb

A cross-platform open-source library for predicting the behavior of web users. WBPL allows training prediction models from server logs. The proposed library offers support for three of the most used webservers (Apache, Nginx and Lighttpd). Models can then be used to predict the next resources fetched by users and can be updated with new logs efficiently. WBPL offers multiple state-of-the-art prediction models such as PPM, All-K-Order-Markov and DG and a novel prediction model CPT (Compact Prediction Tree).  Experiments on various web click-stream datasets shows that the library can be used to predict web surfing or buying behaviors with a very high overall accuracy (up to 38 \%) and is very efficient (up to 1000 predictions /s).

_This library is still a work in progress_

As of today:
The library allows users to run experiments on a specified set of datasets (available in the IPredict repository) with a number of sequence prediction algorithms.
The library allows near real-time prediction
The library can convert log files from Nginx, Apache and Lighttpd to sequence files for experiments.
