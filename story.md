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

But the facility markers were all looking the same. I at least needed to distinguish them by the color. LeafletJS documentation explains how to create custom icons for markers TODO, but since I'm no graphic designer, that looked like quite an effort.

Luckily, I've found yet another plugin for LeafletJS - the Awesome Markers plugin. This plugin delivers exactly what it promises - awesome markers. I've added the plugin and could then change colors of the facility markers based on the state:

TBD

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

Now I needed to allow people to subscribe to updates.

First I thought that tehre should be some kind of "subscribe via mail" option on AWS SNS. So that the user could send an e-mail to some technical address of AWS SNS providing the topic of interest. In this case everythin would've been handled by AWS SNS and I only needed to render a `mailto:...` link.

I couldn't find it in AWS SNS docs so I've asked on SO:

[How to subscribe to Amazon SNS topic with e-mail endpoint via e-mail?](http://stackoverflow.com/questions/34606345/how-to-subscribe-to-amazon-sns-topic-with-e-mail-endpoint-via-e-mail)

Finally it appeared that there is not such functionality on AWS SNS - so I had to implement subscriptions in the backend.

I wanted to keep strict separation of the backend and the frontend, this meant I need an API for subscriptions. I've decided to do it in pure REST style. That is, "subscribing to a facility" would be "creating a e-mail resources under facilitys subscriptions". Consequently implemented as a PUT method operation:

```
PUT http://api.aufzugswaechter.org/facilities/10213788/subscriptions/email/my%40email.com
```

The backend used AWS SNS APIs to create a topic (`facility-10213788`) and then an e-mail endpoint for this topic. At this pointed I really started to like AWS Java SDK, it's simple and easy to work with. Reasonable assumptions and conventions, for instance if the topic already exist, it will not be created again and there will be no error reported. You'll just get an existing topic back.

In aftermath, I probably should have used a different service for e-mail subscriptions. AWS SNS seems to be tailored for the administrative stuff like "your server is down", it is not intended for mailing lists and such. There is no possibility to customize the e-mails, provide a sender etc. These features were not essential for the prototype but I'll have to switch to something else (SendGird?) later on.

## Adding Bootstrap

Now I needed to add e-mail subscription from to the website. Probably I'll need more UI features in the future and I wanted to try out one of the modern UI web frameworks, so I've decided to use Bootstrap. A team colleague used it very successfully on the first DB Hackathon TODO.

At this point I have to say I'm no web designer. I surely know some HTML and some CSS, but that's more or less it. So Bootstrap wasn't easy for me. After a couple of hours it was clear that I'm not getting the results I want. I wanted a pretty-looking website with a LeafletJS map, few forms and dialogs, but I had no time to learn Bootstrap in-depth.

Happily, I've found [Bootleaf](https://github.com/bmcbride/bootleaf) which is a very good Bootstrap template for LeafletJS maps. It contained everything I needed (and more) so I've cloned it and customized for my purposes. "Customization" was basically throwing out everything I didn't needed and moving the scripts I already had in.

This worked quite well, I had a reasonably well-looking Bootstrap-based website with the map of elevators I already had.

## E-Mail subscriptions form

With examples from Bootleaf and Bootstrap documentation I've build a very simple form for e-mail subscriptions. This form can be opened from the facility markers info window. A hidden field carriers the equipment number of the facility, the user can type in his e-mail in the field. Form action makes a PUT request to add the e-mail to the subscriptions of this facility using the REST API I've previously developed.

To save time, I've decided to do no form validation like if the e-mail address is filled and has a correct format.

## Checking for robots

Having implemented the e-mail subscription form, I've got second thoughts. My backend did not check for robots. So basically the API could have been misused to start subscriptions for any e-mail address automatically. The AWS SNS still asks to confirm the subscription from the target e-mail, so you can't be subscribed without your consent. But it would be possible to *initiate* subscriptions for any e-mail so you can get spammed with "please confirm your subscription" e-mails from AWS SNS, which is not good.

I've realized I'll need to build in this protection, some kind of CAPTCHA. I never did this before so I took the first thing which came to my mind which was Google reCAPTCHA. It was in the news not so long ago, so would be good to try it out to see how it worked.

Adding Google reCAPTCHA was done in two parts.

First, I had to add reCaptcha to the e-mail subscription form which was pretty trivial. I had to register for usage, add a `div` with a special class and `data-sitekey` attribute and reCAPTCHA API script to the page. When submitting the form, I just called `grecaptcha.getResponse()` to get the token for server-side validation.

Next, I had to extend the API and the backend to consider the reCAPTCHA token. I've ended up with an URI like:

```
PUT http://api.aufzugswaechter.org/facilities/10213788/subscriptions/email/my%40email.com/?token=<reCaptcha response>
```

The backend had to make a request to a Google reCAPTCHA REST API to validate this token.

Here I also had a small fight with Spring MVC. I first wanted to have the following URI: 

```
PUT http://api.aufzugswaechter.org/facilities/10213788/subscriptions/email/my%40email.com?token=<reCaptcha response>
```

But it appeared that Spring MVC interpreted the last `.com` as a file extension and cut it out. It is possible to reconfigure it, but I've decided to add a trailing `/` into the request mapping ( `.../subscriptions/email/my%40email.com/?token=...`) and it solved the problem.

I've used Spring's `RestTemplate` to invoke Google reCAPTCHA REST API (next time I'll use [Feign](https://github.com/Netflix/feign) for this). The "site secret" was once again provided from a secure S3 bucket. Validation worked fine, now e-mail subscriptions were reCAPTCHA-protected.

## Getting more time

Now I've got the website running pretty well, with all the notifications popping out in the Twitter and delivered via e-mail. I still had a couple of days of the contest, but with the holidays over and "business as usual" starting that week, that was not enough to develop a mobile app. Especially since I had no previous experience developing native Android apps.

I've contacted my department manager and explained the situation. I've shown what I got so far, explained what my learning goals are and asked if I could get one or two days free to finish the development. My boss was imediately excited about the whole story and pretty impressed with what I've got already working, so he supported me immediately. I had two additional days free to invest into the development, that should have been enough.

## Getting a mobile phone

The funny part was that I didn't even had a mobile device to start with. My private hardware is ThinkPad notebooks and iPhones. So I either needed an Android phone or a Mac to develop for iPhone. Since an Android phone is easier to get and Java-syntaxed Android apps were closer to my previous exprience, I've decided to develop for Android.

I've asked on a Twitter and a few colleagues offered to lend me a Android phone. I've borrowed a Nexus 4 and was ready to go.

## Starting the mobile app development

I've downloaded the Android Studio and found out it's IntelliJ-based. I used IntelliJ products in the past, using WebStorm for JavaScript-development but for my daily job I'm using Eclipse. Even after I've switched shortcuts to Eclipse, it felt really awkward. But as an Anroid newbie I was bound to the tutorials and guides most of which were made for the Android Studio. I've decided to stick with that for the moment. I still have to decide if I want to go dual-IDE (IntelliJ IDEA is really very good) or just stay with the Eclipse.

My idea for the app was a simple subscription form were you could type in elevator's equipment number to subscribe to that. It would be also cool to add a QR-code reader. But then I saw a Google Maps app template and decided to start with that instead. Most people are visual, not digital and maps provide good visual impression for very small effort.

I've started with the Google Maps template and got some basic code generated. It looked understandable, I could navigate classes and resources but was in "Cargo Cult" mode. That is, not understanding what's happening, just mimicking what I saw and hoping it'll work. That's completely ignorant but I really did not have time for any theory.

Somehow I also did not manage to make the device emulator work. Probably my ThinkPad is too old to handle it, but again I did not have time to dig into that. But the generated app ran (and was debuggable) on the real device so that was good enough.

I've changed the title and focused the map on the same location as the website and had my first success with a working Google Maps app, branded "Aufzugswächter", running on the borrowed Nexus 4.

## Adding markers

The next thing was adding markers. I've checked the Google Maps for Android guide to see how to add markers and also some other sources to find out how could I download and parse JSON data. Both were quite easy and I got it running quite fast. I had a 100 green elevator markers on the map.

TODO links auf guides, screenshot

## Info windows

Next I've checked how to make an info window on the markers. First I've just added title and text which was already good enough to see at least some info on the elevators. But then I've checked how to actually create a real info window with UI widgets.

TODO

It actually took me a while to figure out how the pieces are fitting together. What is the layout, what are the resources, how is all that composed and so on. I had a number of epiphanies and also a growing respect for the Android platform. My unerstanding of Android started to grow slowly and "Cargo Cultness" began to decrease.

## Subscribing to push notifications

With the basic app running and showing a map with clickable elevator markers, I could now actually start working on subscriptions for push notifications. This had apparently two parts - subscribing to facility topics from the client and sending a message to these topics from the server. Sending looked quite trivial (a HTTP POST of the message to some Google server) which I could test with `curl` from the command line. Subscribing was more risky for me.

First I've registered for Google Cloud Messaging and got my keys.

TODO link

I've also checked the Google Cloud Messaging guide for Android TODO and their sample client. "Cargo Cult" mode on, I've copy-pasted code which seemed to be relevant and managed to subscribe for a few facilities. Sending messages using `curl` also seemed to work, I was hitting the corresponding breackpoints in code, so the basics worked fine.

## Triggering subscriptions from the marker info windows

Now I had to somehow trigger subscriptions from the marker's info windows. I've added a button to the layout and started to search for the way to handle the button click event.

Finally it appeared that markers info windows aren't "real" UI elements. I found no way to handle any events from the widgets inside the info window layout. It looks Seems like Google Maps just renders the layout and then uses the rendered image to show it in a popup window. So there was basically no way to hanlde a press event of an individual button there.

After some googling and reading a few answers on SO I've found out that widgets inside the info window don't react to any events, but the window itself does. I've decided to use the "long click" event. That is, the user will have to click/touch and hold the window for a couple of seconds to trigger the action. That was basically the simplest I've come up with to solve the problem - but later UX tests showed that users actually liked this typed of interaction. I mean, my wife said it was good.

TODO some code

## Handling subscriptions

No that I had an event handler for the long click event, I could actually start a subscription for the selected facility. For this I had to keep a mapping of marker -> facility and then, when the long click event was triggered, start the GCM subscription.

TODO code

I thought it would be also very helpful to display some notification on the screen when the subscription was successful. I've recalled I saw something like `Toast`s in the example code and looked it up. It appeared to be exactly what I needed, with `Toast`s I could "bake" and display a notification on the screen.

## Handling push notifications

Next thing was actually handling the push notifications on the device. I wanted these notifications to pop up on the screen (just as notifications from Google Mail etc. do). This required a few more actions in the "Cargo Cult" mode but it started to work quite fast.

TODO

It took me some time to figure out how to make these notifications to appear on the top of the screen. Finally, with the help of a few blog posts I've figured out a combination of parameters which made notifications reliably appear in all modes.

TODO

## Handling unsubscriptions

If you can subscribe, you should be able to unsubscribe as well. I needed to modify the marker info window to display subscribe or unsibscribe button depending on whether the users is subscribed to this facility or not. For this, I needed to know the subscription status.

It appears that there is no way to check the topic subscription statis with GCM. This creates two problems.

First, I need this status to present the corresponding UI.

Second, there's really no way to reliably check if the unsubscription action was ultimately successful. This means the user may still get notifications for the facility he or she unsubscribed from.

This appeared to be a known limitation. The solution to that was to save subscription status on the device and check the topic of the notification. In case we're getting a notification for the topic the user is no longer subscribed to, this would mean that something went wrong with unsubscription and we have to silently trigger unsubscription once again.

Saving the state of subscriptions got me acquainted with preferences. They appeared to be *the* way to share data between different components on Android - I've asked about that on SO:

TODO

## Integrating GCM into the backend

Now that my mobile app could subscribe (or unsubscribe) to individual facilities as well as handle the incoming push notifications, it was time to channel my facility state updates messages via GCM.

My initial plan was to use AWS SNS for that. AWS SNS supports GCM endpoints, so I thought I'll let AWS send my GCM messages.

But it did not quite work the way I thought.

First, I somehow could not create a GCM topic as AWS topic subscription endpoint. It looked like I need to create endpoints for individual recipients (basically per-device). That was too much to manage.

Next, I did not find a way to send structured messages. I needed to send more that just text, I needed to send a structure with the facility equipment number, state etc.

So at this moment it started to look like AWS SNS is not the right tool for the job. This echoed with some experiences from the e-mail subscriptions (you can't set the sender or format "nice-looking" mails) and also what I read on SO. Now my impression of AWS SNS was that it's really just for administrative notifications from the AWS infrastructure and not meant for the end-users.

Finally, I decided not to use AWS SNS for GCM messages. But the e-mail part worked reasonably well so migrating from that part from AWS SNS to something else wasn't necessary. I've decided to leave e-mail subscriptions on AWS SNS for the moment.

With these decisions, I had to send GCM messages on my own.

I've checked Apache Camel, but to my surprise found no GCM component there! That was unexpected, both Apache Camel and GCM are major things, so it was a reasonable to expect Apache Camel to have a Google Cloud Messaging component/endpoint.

I've looked around and found a camel-google-cloud-messaging TODO project on GitHub. The code looked really good, but after closer examination it appeared a bit outdated. There were some changes in the GCM server-side API which were not reflected in that project. Another problem was that project license was not clear (at that moment), so it was legally not safe to use it or extend it.

Since I already had some experience writing Apache Camel components, I've decided to implement my own GCM component for Apache Camel. But I needed a Java client for GCM server-side API for that. True, that's a simple HTTP POST interface, so some pragmatic implementation would have worked. But I couldn't imagine that there's no ready-to-use Java client for GCM.

Indeed, Google provides such a client and hosts it in an open-source GitHub project:

TODO

The only problem was that I needed a Maven artifact to use it in my builds and that client was built with Ant - no Maven POMs, no deployment to the central Maven repo.

To solve it, I've created `pom.xml` files for Maven and send them to Google via pull request (TODO), first signing a Google contributor agreement TODO. So now I'm an official Google contributor (would it look good in my CV?).

For the time being I had to use the snapshot artifacts of GCM client I've built. By the way, Google people reacted quite fast, Google GCM client is now available as a Maven artifact from the Central Maven Repository:

TODO

TBD
