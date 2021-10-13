# WorkLance
CSE 3224 Informative System Design And Software Engineering Lab 
## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Features](#features)
* [Status](#status)

## General info 
This project is useful for the search of worker at anywhere the user want. User can hire workers for user's work within the area user live. With this project the problem of bargaining wont be a issue. Worker will get more work opportunities than before with this project and user can hire workers at user's selected location.  
	
## Technologies
Project is created with:
* **Android Studio 3.6**

Language used in this project is:
* **Java**
	
## Setup
To run this project, there are some prerequisites:
```
* API of emulator is required 30 or later.
* Minimum 2 emulator or device combination is required to see the project functionality. 3 emulator or device combination is recommended for within area search.
* internet connection to emulator or device is required. 
* Google Play Service is required on selected emulator or device for project functionality.
* Require user permission for further functionality.
-Approximate location(network-based)
-Precise location(GPS and network-based)
-Modify or delete the contents of your SD card
-Read the contents of your SD card
-Full network access
-Receive data from internet
-View network connections
-View Wi-Fi connections
-Prevent phone from sleeping
-Play Install Referrer API
```
After the prerequisites, clicking `run` on **Android Studio** can run this project on selected emulator or device combination.

## Features
* User can **Login** and **Registration** into this project via `Login` and `Registration` activity after the splash screen.
* After **Login** or **Registration** into the application a individual `Profile` will set up for user. Where user can edit their profile with required changes
* user can change his location by `Change Location` option on `Home` activity.
* In `Request` activity user can add **Title**,**Description** and **Select require worker type**. After submission the request a **Notification** will go to active workers whom are available and capable for the requested work.
* After receiving **Notification** worker may place his service fee for that requested work in `Incoming Request` activity. 
* If user agree with service fee from serviceman, a **Confirmation Notification** will go to selected serviceman.
* Serviceman can **Start** or **Cancel** work with `Start Work` or `Cancel Work` option.
* A activity `Work In Progress` can show the ongoing work.
* After completion of work user can `Finish Work` and give `feedback` to serviceman.
* A `history` option is available to see previous work.
* Both client and serviceman can give `App Feedback`
* Both client and serviceman can see some details about the developer on `About Us` activity.
* Both client and serviceman can `sign out` if needed.

## Status 
Project is recently developed with currently required and possible features.



