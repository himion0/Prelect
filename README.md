Tweet-mining
=======

An application to anaylse tweets 

Implemented in Java, purpose of the application is to allow a user to scan twitter for tweets related to the keywords in specified text file (Default: keywords.txt) and put them into a format easily accessible by other applications like R.

[GUI] How to use:
=======
**Load Keywords**

•	Will load the keywords from the specified file. Please not that the file must have a .txt extension. 
•	Keywords must be of @... or #... format. 
•	Each keyword must be on its own line:

			@Twitter
			#Happy

**Save to Text File**

•	The application will save the tweets it has found into the text file location specified. It will be in a format that is easily imported into R.
•	Note that each time the application is loaded; it will load the previously saved tweets. 
•	Delete previous tweets button?


**Keep Running**

•	Will keep the application constantly running after hitting ‘Search Twitter’. When the Rate Limit is hit it will pause and add the tweets that it has found so far to the data.
•	To stop hit the ‘Stop’ button (The search twitter button will change once it has been press)

**Search Twitter / Stop**

•	Search twitter using the keywords file loaded and will display information about what tweets were found in the Console Display on the right

**Console Display**

•	Shows information about what the program is doing:
o	Tweets found along with the keywords searched for
o	Rate Limit Reached warnings
o	How many tweets currently are in the data set. 
o	Errors


[API] How to use:
======

**Combine Data**
This allows you to combine data in a certain folder. It will find all the files in the directory specified in the first argument of the constructor  (e.g. "C://Users/${USER}/Directory") with a general file named with the specified second argument "Data" (e.g. .bin, Data 2.bin...)

**DataController** 
This class has a main method to allow running the entire prorgam from the terminal the user to deploy the application for tweet collection from a server. [TO BE IMPLEMENTED] GUI setup of downloading tweets locally from server or starting a server from a client.

Conclusion
======

The application should assist users with minimal programming skills to search Twitter for tweets relative to their needs while allowing for some basic NLP to detect whether tweets are positive or negative. It has mainly been designed to be use in conjunction with R-Studio however it could be injected into other programs after fiddling with settings.


Libraries included:

- [Stanford NLP](http://nlp.stanford.edu/index.shtml)
  A fantastic library developed by the Stanford Natural Langauge Processing group at Stanford University.
  
- [twitter4j](http://twitter4j.org/en/index.html)
  A library for connecting to the Twitter API


