# IPredictTheWeb

A cross-platform open-source library for predicting the behavior of web users. WBPL allows training prediction models from server logs. The proposed library offers support for three of the most used webservers (Apache, Nginx and Lighttpd). Models can then be used to predict the next resources fetched by users and can be updated with new logs efficiently. WBPL offers multiple state-of-the-art prediction models such as PPM, All-K-Order-Markov, DG, and CPT (Compact Prediction Tree).  Experiments on various web click-stream datasets shows that the library can be used to predict web surfing or buying behaviors with a very high overall accuracy (up to 38 \%) and is very efficient (up to 1000 predictions /s). This library was first introduced at the conference ISMIS 2014 in Denmark [1].

_This library is still a work in progress_

## Run an experiment

An experiment consists in comparing one or more sequence prediction models on a dataset. A web GUI is available to help you setup an experiment.

###Prerequisite on the server (for Debian or Ubuntu):
A web server which can execute PHP such as Apache or Nginx.

`$ sudo apt-get install apache2 php5`

Java 8 installed

`$ sudo add-apt-repository ppa:webupd8team/java`

`$ sudo apt-get update`

`$ sudo apt-get install oracle-java8-installer`

Screen or equivalent installed

`$ sudo apt-get install screen`

###Steps:
Clone this repository on your server in a directory accessible via HTTP (e.g.: /var/www/)
`$ mkdir /var/www/IPredict`

`$ git clone https://github.com/tedgueniche/IPredictTheWeb/ -C /var/www/IPredict/`

Run the java binaries in a screen

`$ cd /var/www/IPredict/java`

`$ screen`

`$ java -jar release/IPredictTheWeb_last.jar`

Access the web GUI (/var/www/IPredict/www/index.html) with you browser.


![Web GUI](http://s13.postimg.org/6a19n2h07/www_GUI_1.png)

Choose a data source (pre-made or custom) and the number of sequences to consider. Pre-made datasets are available in the application and described in [1,2]. A custom log file from one of the three supported platform (Nginx, Lighttpd, or Apache) can be uploaded.

Select one or more prediction models for comparison. Each model has its own set of parameters that needs to be set. All the predictors do not perform equally well in all situations. Therefore, it is recommended to test them all for a given dataset.

Select parameters to indicate how to compare the selected prediction models. These parameters are independent of the choice of prediction models.

Click on the "Start Experiment" button to launch the experiment. All the parameters are sent via ajax to a PHP script (www/run.php). The request is then interpreted and executed by the IPredict framework. The results are returned to the GUI in JSON.


![Results](http://s16.postimg.org/4jebuiahh/www2013_Performances1_1.png)




[1] Gueniche, Ted, et al. "WBPL: An Open-Source Library for Predicting Web Surfing Behaviors." Foundations of Intelligent Systems. Springer International Publishing, 2014. 524-529.

[2] Gueniche, Ted, Philippe Fournier-Viger, and Vincent S. Tseng. "Compact Prediction Tree: A Lossless Model for Accurate Sequence Prediction." Advanced Data Mining and Applications. Springer Berlin Heidelberg, 2013. 177-188.
