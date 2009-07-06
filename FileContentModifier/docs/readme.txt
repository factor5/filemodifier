FileContentModifier v1.1
----------

FileContentModifier is an Java application for manipulating content of text files. 
Because it is written on Java it is supposed to be working under most OS. For now it is
tested only under WindowsXP.

FileContentModifier Copyright (C) 2008-2009 Svilen Velikov.


The main features of FileContentModifier: 

  - Configuration through config file.


Use and configuration instructions:
  - FileContentModifier is provided as an executable Java jar archive. You need
    an Java virtual machine in order to run this application. You can get Java
    from 'http://www.java.com/en/download/index.jsp'.
  - Configuration file must be in the same directory as the application and to have name
    'config.properties'.
  - Configurations that may be done through the config.properties file are for example as
    follows:
    
	fileExt=txt	: extension of the files to be scanned.
	prefix=_	: prefix for the key names for the strings to be replaced.
	_str=<i>	: example string key '_str' and value '<i>' that application
			  	  will find and remove.
	_str=<i>|<b>: this tells the application to replace all '<i>' strings with '<b>'. 
  - FileContentModifier recursively scans the directory where it is situated and finds all
    text files with provided extension. Reading all found files it removes the required
    strings.


FileContentModifier is free software distributed under the GNU LGPL.


  This distribution contains the following files:

  FileModifier_v1.1.jar		- FileContentModifier executable application
  config.properties		- Configuration file for tunning the application
  readme.txt			- This file
  Hystory.txt			- Hystory of this application

---
End of document