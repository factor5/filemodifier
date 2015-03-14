FileContentModifier is an Java application for manipulating content of any kind of text
files. Because it is written on Java it is supposed to be working under most OS.

## The main features of FileContentModifier: ##

  * Setup through configuration file.
  * Possible backup for the files before the changes to be done.
  * Windowed console where all the activities of the application is tracked down.


## Use and configuration instructions: ##
  * FileContentModifier is provided as an executable Java jar archive. You need an Java virtual machine in order to run this application. You can get Java from 'http://www.java.com/en/download/index.jsp'.
  * Configuration file must be in the same directory as the application and to have name 'config.properties'.
  * Configurations that may be done through the config.properties file are for example as follows:
```
fileExt = txt	: extension of the files to be scanned.
prefix = _	: prefix for the key names for the strings to be replaced.
_str = <i>	: example string key '_str' and value '<i>' that application
                  will find and remove.
_str = <i>|<b>  : this tells the application to replace all '<i>' strings with '<b>'. 
```

  * FileContentModifier recursively scans the directory where it is situated and finds all text files with provided extension. If the user decides to make backup it has the option to do that. Next reading all found files it removes or replaces the required strings.