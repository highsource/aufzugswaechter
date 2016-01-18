# Developing Aufzugswächter #

# Introduction

["Aufzugs-API"-Contest](https://www.mindboxberlin.com/index.php/contest.html) announced on the [3rd DB Hackathon](https://www.mindboxberlin.com/index.php/3rdhackathon.html).

The time frame was between 12.12.2015 and 10.01.2016, basically Christmas and Silvester time. Initially, I did not wanted to participate. But then I hade this idea: wouldn't it be cool if you'd be able to get notifications about broken elevators? This would be good for disabled or elder passengers to know if elevators work on their stations. Looking it up every time is time consuming, but push messages would be quite helpful.

This would be also a good project to learn a few new things:

* Amazon Web Services - I'll host on AWS and use a couple of AWS services for development, also try out AWS SDK for Java in practice.
* Apache Camel - I'll have to do with messages so I'll use it for message-based intergation tasks, needed to learn it anyway for one of the projects.
* LeafletJS - I'd use it for mapping. I know OpenLayers pretty well, but LeafletJS must also be very good and much lighter.
* Native mobile development - I'll probably need to write a mobile app to get push notifications. I've developed mobile apps but using PhoneGap/Apache Cordova, not native ones.
* "API-based" architecture - I'll implement an API-controlled backend and separate mobile and web presentations.
* Bootstrap, Angular - I'd use Bootstrap and/or Angular to implement the web front-end.

# Architecture

* Backend - accesses the Aufzugs-API, provides data via REST API.
* Web - static web client, accesses the REST API of the backend.
* Mobile - native mobile app, also access the REST API of the backend.

# Development story

## Name

First of all I needed a good name. I was thinking about "someone who watches the eleveators", that'd be Aufzugswächter in German. The domain `aufzugswaechter.org` was free so I took the name and boughtregistered the domain on Jocker.com.

## Java client for the Aufzugs-API

The Aufzugs-API is desribed using a YAML file:

* [SSTBT_REST-API_ADAM_1_contest_alpha.yaml](https://www.mindboxberlin.com/index.php/contest.html?file=files/cto_layout/downloads/opendata/SSTBT_REST-API_ADAM_1_contest_alpha.yaml)
 
I'll need a Java client for this. Apparently [Swagger](http://swagger.io/) can do this, at least the contest page references Swagger.

As probably other people may need a Java client as well, I've implemented it as an open-source project:

* https://github.com/highsource/db-elevator-api-client

And immediately released to the Maven Central repo.

The only problems with Swagger were:

* Swagger code generator generates not just Java files but also `pom.xml`, directories, etc. I had to clean it up a bit.
* Swagger did not detect a few enums correctly. Here's my Stackoverflow question on that:  
[How to supply inner type for the array parameter in Swagger?](http://stackoverflow.com/questions/34544243/how-to-supply-inner-type-for-the-array-parameter-in-swagger)

Apart from that, it worked very well, the client is generated fully automatically, there are DTO classes as well.

## Analyzing Aufzugs-API

TODO add links

* Reports the currents state of facilities (elevators, escalators), no way to see changes. This means I'll need to periodically query and detect changes on my own. This will be done on the backend.
* Facilities do not contain info on the station, just a reference (`stationnumber`). So I'll need to query for facilities as well as for stations and merge the data later on. Would be also better to do it on the backend to make clients lighter.
* Facilities have coordinates but they're not in [GeoJSON](http://geojson.org/) format. GeoJSON would be better for web mapping. I could produce GeoJSON in the backend. 
* Just 100 elevators at the moment. How many will there be? Answe via Twitter: up to 3000. This means if I do web mapping I'll have to do some clustering of markers, displaying 3000 markers at once is not a good idea - neither the user nor the web/mobile client will be able to handle it well.
* Seems like they have some limits in the API! With too frequent requests I'm getting HTTP `429` response. This means I'll have to build the quering on the backend fault-tolerant. To expect every request to Aufzugs-API to fail and hanle it gracefully.

## Backend based on Spring MVC

From the experience with past DB Hackathons, Spring MVC works perfectly for REST backends. You get results very fast and with minimum configuration. You can also produce a standard WAR artifacts which can be executed in almost any web container (Tomcat, Jetty). This also helps to get it up and running on AWS Elastic Beanstalk very fast. I had very good experience with AWS Elastic Beanstalk on the 3rd DB Hackathon.

I've used the DB Elevator API Client I've built befor to access the Aufzugs-API. Then I've implemented DAOs to store stations, facilities and facility state reports (state of the facility at a certain timestamp). To save time, I've implemented momory-only DAOS, no database storage. This makes the backend a little bit fragile (you loose the state with restarts), but that's an acceptable drawback to save a time.

I've implemented a watcher service which queries the Aufzugs-API once in a minute and checks for state changes. In case of state changes, it fires state change events. Registered listeners can react to these events and for instance send messages.

The backend also implemented a REST-based API which allows getting the state of the facilities:

TODO link

But it also supports the `updatedSince`-parameter and then only shows facilities changed since the given timestamp:

TODO link

This will be useful clients to periodically query for changes.

## Deploying the backend on the AWS Elastic Beanstalk

My goal is to implement a fully functional prototype open for everyone. I wanted to make things available for other people from the very start. So I needed to host the backend I'm developing - with the least possible effort.

I had very good experience with AWS Elastic Beanstalk on the 3rd DB Hackathon. You can basically upload a WAR file an get it running within minutes.

I've created a new environment for Aufzugswächter, also configured the Elastic Beanstalk domain TODO and added an ANAME-Record on Joker to make it api.aufzugswaechter.org. After uploading the WAR I could test queries in less than 2 minutes.

## Website

I've desided to make a fully static website which would use the Aufzugswächter API for all the operations. I have good experience with GitHub Pages. I's gread for static hosting, you have `git` repor for your web files.

I've configured `ANAME`/`CNAME` records with Joker.com to point aufzugswaechter.org and www.aufzugswaechter.org to the GitHub pages. For `CNAME` it's also necessary to create a `CNAME` file in the `gh-pages` branch:

TODO

I've rolled some vanilla `index.html`, committed/pushed it all and had a basic `aufzugswaechter.org`-website.

## Facilities on the map

I wanted to try LeafletJS out so I've implemented a simple map and put facility markers on it. This was pretty easy, the JSON returned by my backend is a valid GeoJSON which contains all the properties of the facilities including information on the station. I've checked a few examples and added info windows for markers. That was very easy, I only had to build the contents of the info box as HTML for each of the markers.

Next, I wanted to make cluster markers. This will be really necessary when there'll be 3000 facilities. And marker clusters always look cool.

It appears that there is a LeafletJS plugin for that:

TODO

It took just a few lines of code to add marker clusters. The map started looking pretty cool, with animated marker clusters which "explode" when you zoom in.

## Map imagery from Mapbox

I wanted to use OSM map material, but also some good-looking rendering. I knew Mapbox provides it so I've registered on Mapbox and got my own public API key. Configuration on LeafletJS is trivial.

## Twitter bot

At this point I had a functioning backend and a web page which was displaying the current status of the facilities. But I needed to move towards notifications. Ultimately I wanted to implement mobile push notificatons. But first probably something simpler.

What can be simpler? E-mail? Yes, no need to implement a mobile client but still subscriptions. How about a Twitter bot? That should be easy and I can get Apache Camel up and running.

Apache Camel apparently had a Twitter component. I was a first-time Apache Camel user so it took a bit of time to figure out the concepts and get the basic "Hello, World!" running.
I've registered a @aufzugswaechter TODO and then a Twitter application, got my access keys and after some experiments I could send messages via Apache Camel and see them appear in the `@aufzugswaechter`s timeline.

## Sharing secrets with AWS instances

But now I had the following problem. How can I safely share secrets with AWS instances? I.e. I just upload one WAR file to AWS Elastic Beanstalk. And I will need to provide secrets like Twitter application secret key or later on other passwords and key (Google Cloud Messaging on the horizon).

I've checked a few blog posts from Amazon and found a good solution there:

TODO

So I did the following:

* Created an AWS S3 "bucket" for Aufzugswächter.
* Put Twitter credentials in `twitter.properties` and uploaded it to the created bucket.
* Configured an AWS IAM "policy" with the permission of read-only access for the `twitter.properties` file in the specific bucket.
* Created a personal user for development, assigned this policy (via group and role), downloaded and saved AWS credentials in TODO `aws.credentials` on the development machine.
* Reconfigured the AWS Elastic Beanstalk instance to have the corresponding AWS IAM policy.

This brings the following result:

* Both my dev machine as well as the AWS Elastic Beanstalk instance have access to `twitter.properties`.
* I don't have to explicitly provide my credentials to the code as AWS Java SDK automatically reads TODO `aws.credentials` which I've previously saved.
*  The AWS Elastic Beanstalk instance also has the corresponding policy via environment configuration.
*  Noone else has access to `twitter.properties` in that buckets so my secrets are safe.

So I can now safely share secrets via S3 buckets, with no explicit configuration of credentials.

## Faking facility state reports

We have just 100 facilities reported by the Aufzugs-API, they don't change state often. That's not good for demo/test purposes, but going out and breaking elevators is also not a good idea.

To make it a bit more interesting I've added "faking" functionality. With the probability of 0.5% I'm "faking" a state of the facility, for instance instead of `ACTIVE` you get `TEST_ACTIVE`. That's a state change which triggers the events to be fired. The probability of 0.5% for 100 Elevators checked every mminute means that every one or two minutes there will be an event.

## AWS SNS

Twitter bot simply reports every state change. This is a good test but it's not facility-specific and not target to the individual users. Ultimately I wanted push notifications for mobile but e-mail would be also good to start with.

I also wanted to experiment with AWS services, so I took a look at AWS SNS. That looked fine, I've tested the e-mail endpoint and got mail with nice unsubscribe link. AWS SNS also supports push notifications (including Google Cloud Messaging). Apache Camel also had a standard AWS SNS component so I've decided to use AWS SNS.

## Using AWS SNS with Apache Camel

To deliver individual message for individual facilities I'll need a singe topic per facility. Unfortunately, the standard Apache Camel SNS component could not handle that: I would have had to preconfigure all the routes for all the facilities.

So I've implemented my own "dynamic" SNS component for Apache Camel. This component could read the topic from the message header, create this topic with AWS SNS if required and the send the message to this AWS SNS topic.

This worked, I've subscribed to a few of the topics via AWS Management Console and started receiving e-mail notifications.

## E-Mail subscriptions
