<!--
This README intends to be a starter template for the Pearson Student Coding Contest. Feel free to add or omit content as needed for your app. The formatting is done using Markdown. These comment sections are simply guides that you can delete.
-->

# "ClassMap"


<!--
The "App Overview" section intends to be a high level description of your app. Think of what you might want to know if considering a purchase in an app store. 
-->

## App Overview

"The use of imagery and keywords are two of the strongest techniques for learning a new topic or subject. These two techniques can be used by people with learning disabilities, students learning foreign languages, and just about anyone else struggling with new subjects. The concept of using imagery and keywords is the whole idea behind mind maps. Now imagine, a mind map where students in a particular class could post keywords, images, and other useful items to an ever growing, ever changing community mind map. A mind map that is expanded and shaped by the singular mind of a class! This is the idea behind our application, ClassMap.
ClassMap will consist of a drag and drop interface to allow for a more user friendly experience. To offset the clutter that multiple users will undoubtedly bring to a mind map, ClassMap will feature an "upvote" system to allow the most helpful information to appear more prominent, while hiding "less" helpful information. A mind map can't grow if a student does not understand the keywords they should expand on. Using Pearon's dictionary APIs, we will give meaning to complex keywords that may be difficult for some students to understand, allowing for greater expansion, and in the end, better understanding. In the end, the teacher should have a map of the brain of the class. By observing keywords, images, files, and how they were expanded on, the teacher will be able to see which topics need to be focused on more and hopefully be able to touch on those topics in a more relatable way to the student.

### Planning

[See this project's hackathon entry page!](http://www.hackathon.io/pearson)

### Demo

[Watch a video of the application in action!](https://www.youtube.com/watch?v=8BFMaQjrw4s)

### Screenshots

![Login Screenshot](http://developer.pearson.com/sites/default/files/LSDashboard_Login_small.png)
![Launch Screenshot](http://developer.pearson.com/sites/default/files/LSDashboard_NewActivity_small.png)
![Settings Screenshot](http://developer.pearson.com/sites/default/files/LSDashboard_Settings_small.png)


<!--
The "App Details" section intends to explain how your app works. Describe the major components, what APIs were used, and what is missing to make this production ready.
-->

## App Details

"ClassMap consists of a drag and drop interface. Students and teachers can add a variety of node types to the map including, Text Nodes, Image Nodes, and YouTube videos. Right clicking on a node will bring up it's content in a larger window for easier viewing. An upvote system ensures you can always do away with 
nodes you may not be interested in. Once and user has upvoted a node, they have the option to click the home button, bringing up their personalized ClassMap.

### API Usage

 * [LearningStudio API](http://developer.pearson.com/learningstudio/course-apis/course-info/enrollment/reference) - provides class schedules and rosters
 * [LearningStudio Eventing](http://developer.pearson.com/learningstudio/receive-events) - provides realtime notifications of class happenings

### Scope of Functionality 

This application is mostly functional. The number of users able to use this application is limited by shortcuts taken for data storage. These were necessary to finish by the deadline, but replacing the temporary data store with a more scaleble solution would make this application ready for prime time!

<!--
The "Prerequisites" section intends to assist someone get started with your source code. They might not be familar with your frameworks or project structure. Help them out by explaining what you already know. 
-->

## Prerequisites

### Build Environment 

 * IntelliJ 
 * Swift 1.2 is required
 * Java 7 or greater is required
 * Maven 2 is required

### Server Environment 

 * Any app server supporting the Java 2.5 Servlet Specification

<!--
The "Installation" section intends to assist someone deploy your project themselves. What do they need to configure, package, and distribute?
-->

## Installation

### Application Configuration

JavaServer/MyContestApp/src/main/resources/LearningStudio.properties

~~~~~~~~~~~~~~
application_id={Application Id}
client_string={Client String}
~~~~~~~~~~~~~~

iOS/MyContestApp/LearningStudio.plist

~~~~~~~~~~~~~~
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>app_id</key>
    <string>{Application Id}</string>
    <key>client_string</key>
    <string>{Client/Environment Identifier}</string>
</dict>
</plist>
~~~~~~~~~~~~~~

### Application Deployment

#### Build

Java Server

~~~~~~~~~~~~~~
cd server
mvn clean package
~~~~~~~~~~~~~~

iOS App - build with xCode


#### Deploy 

Java Server - copy target/my-contest-app.war to the server

iOS App - run in emulator

<!--
The "Credit" section intends to highlight your team. Tell who contributed to what parts of the project. Give thanks to mentors that were helpful.
-->

## Credit

### Team

This project was a collaborative effort. We are all classmates in CS101 at Cool University.

 * [Aaron Martin](#) - marketing and presentation
 * [Jamie Davis](#) - graphics and videos
 * [Travis Clinkscales](#) - iOS application
 * [Soumith Thumma](#) - java server application
 * [John Tran](#) - java server application

### Other

This project would not have been possible without [Professor Kumar](#). They informed us of the coding contest and acted as mentored during the entire process.

<!--
The "License" section intends to be a license declaration. Checkout choosealicence.com to become familar with different licences. The full license should be included in the LICENSE file, but you can also declare and link to it here.
-->

## License

My Chosen License

~~~~~~~~~~~~~~
http://choosealicense.com/
~~~~~~~~~~~~~~
