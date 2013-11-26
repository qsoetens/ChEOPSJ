ChEOPSJ
=======

Software is an ever-changing entity and frequently executed unit tests are the primary means to increase the confidence that the changed system continues to work as expected. Executing an entire test suite however can take a significant amount of time; much longer than developers are willing to wait before tackling the next change action. Our tool prototype ChEOPSJ may alleviate this problem by adopting a change-centric approach as it sits in the back of Eclipse and captures all changes made in the main editor while the developer is programming. The changes and the dependencies between them are analyzed to deduce which unit tests are relevant for a selected sequence of changes. 

ChEOPSJ is conveniently made up of several eclipse plugins:

be.ac.ua.ansymo.cheopsj.model
  This is the plugin that defines the model for both changes and source code. The model is persisted through hibernate and stored in an local file based HSQL Database.
  The source code model is based on the FAMIX model and represents structural source code entities like packages, classes, methods, fields, etc. ... The ModelManager class contains methods to store and retrieve source code entities.
  The change model defines tangible entities that act on the source code model. The ModelMangerChange contains methods to store and retrieve change entities from the database.

be.ac.ua.ansymo.cheopsj.changerecorders
  This is the plugin that defines how changes and source code enties are instantiated. This plugin is used by both the logger and the distiller to create changes and store them in the databse through the model plugin. 
  
be.ac.ua.ansymo.cheopsj.logger
  This plugin implements the change recording functionality. It runs in the background of eclipse and records changes a developer is making, while he is programming. 
  
be.ac.ua.ansymo.cheopsj.distiller
  This plugin implements a change recovery approach. It currently only works for SVN. It iterates through all revisions in the version control system and checks its commit message. For each file that was added, it will create additions for every source code entity inside that file. If a file was deleted, it will create removes for every source code entity in that file. If a file was modified, then we use Fluri's ChangeDistiller (org.evolizer.changedistiller, available here: https://bitbucket.org/sealuzh/tools-changedistiller/wiki/Home) to compare the old version of the file with the new one and instantiate the appropriate change entities. 
  
be.ac.ua.ansymo.cheopsj.model.ui
  This plugin provides some user interface (mostly for debugging purposes, to check if recored changes are actually correct). It creates two views: a change inspector that lists all the changes in the model, and a change graph that shows how the changes are dependant of one another. 
  
be.ac.ua.ansymo.cheopsj.testtool
  This plugin implements our change-based test selection algorithm.

The following eclipse projects are part of the branding/update site/features configuration:
be.ac.ua.ansymo.cheopsj.branding
be.ac.ua.ansymo.cheopsj.featuers.core
  Contains the following plugins: model, changerecorders, logger
be.ac.ua.ansymo.cheopsj.features.distiller
  Adds the distiller plugin
be.ac.ua.ansymo.cheopsj.featuers.ui
  Adds the User Interface plugin
be.ac.ua.ansymo.cheopsj.features.testselection
  Adds the testtool plugin
be.ac.ua.ansymo.cheopsj.update
  Defines how the features are grouped on the update site. 
