# Hitchapp

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Hitchapp is an app designed for college students to be able to carpool with other college students. Providing a safe way for both the driver and the rider to save money and help the environment through lower fuel usage.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Transportation 
- **Mobile:** As a hitchhiking app for students, mobile would be the best and only way of using it, as it would need to be used on the go
- **Story:** Connects drivers with riders in order to save money and environmental toll. Made for connecting students who need transportation with students who need gas money.
- **Market:** Any current college student in need of saving money could use this app 
- **Habit:** This app could be used for trips back home from college, grocery trips, going to and from parties, and any other thing college students might need to use transportation for.
- **Scope:** It could start by just matching drivers with riders and later on we could generate popular routes so that both drivers and riders can sign up for them with a computer arranged date/time, which could make it need less coordination from both parties.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

Your app has multiple views
Your app interacts with a database (e.g. Parse)
You can log in/log out of your app as a user
You can sign up with a new user profile
Somewhere in your app you can use the camera to take a picture and do something with the picture (e.g. take a photo and share it to a feed, or take a photo and set a user’s profile picture)
Your app integrates with a SDK (e.g. Google Maps SDK, Facebook SDK)
Your app contains at least one more complex algorithm (talk over this with your manager)
Your app uses gesture recognizers (e.g. double tap to like, e.g. pinch to scale)
Your app use an animation (doesn’t have to be fancy) (e.g. fade in/out, e.g. animating a view growing and shrinking)
Your app incorporates an external library to add visual polish
* Users can sign up with a new user profile with info about yourself/profile pic
* Users can login/logout
* Uses Google maps API to find ETA
* Takes in picture of student ID for safety verification
* Ability to search for rides leaving near you and going to a nearby place
* Car seat count to see how many spaces are left in the car with bar animation to see how full it is
* Chat between driver and rider
* Driver can submit where they're going and riders can request to join
* User can edit profile
* User can view somebody else's profile

**Optional Nice-to-have Stories**

* Integration with Stripe, Square or Venmo for payment
* Notifications
* View reviews
* Write reviews
* Rider can submit where they want to go and driver can request to give ride, which would automatically move it to the "Ride" page so other rides could join
* Users can switch to and from driver/rider mode easily
* Beautiful UI with intuitive animations

### 2. Screen Archetypes

* Login Screen with option to sign up
   * Users can login
* Sign up screen
   * User can sign up with a new user profile
   * Takes picture of student ID for safety verification
* "Ride" screen
   * Driver can submit where they're going and riders can request to join
   * Uses Google maps API to find ETA
   * Has cards with each offered drive and option to request to ride
   * Car seat count to see how many spaces are left in the car with bar animation to see how full it is
* "Drive" screen (stretch)
   * Rider can submit where they want to go and driver can request to give ride, which would automatically move it to the "Ride" page so other rides could join
* Chat screen
   * Chat between driver and rider
* Pay screen (stretch)
   * Integration with Stripe, Square or Venmo for payment
* User profile screen
   * User can edit profile
* Other users' profile screen
   * User can view somebody else's profile
   * Reviews (stretch)
* Review screen
   * Reviews (stretch)
 

   
   
### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Ride
* Drive (Stretch)
* Chat
* Profile

**Flow Navigation** (Screen to Screen)

* Login -> Signup screen if person has no account
* Login -> Ride screen
* Sign up -> "Ride" screen
* Chat screen -> Pay screen (Stretch)
* Profile screen -> review screen (Stretch)
* "Ride" screen -> other users' profile screen
* "Drive" screen -> other users' profile screen (Stretch)

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
