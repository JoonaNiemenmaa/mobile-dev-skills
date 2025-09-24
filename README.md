# Mobile Dev Skills Submission

The project is an Android application that can send and receive emails via a self made implementation of SMTP and IMAP. I'm retaking the course since I forgot to finish the project last semester and ran out of time hence the dates from last year in the learning diary.

The excercise projects are found in `excercises/` and the actual project can be found in `project/EmailClient/`.

## How to run this thing

In addition to running the Android application itself this project also needs a mail server capable of SMTP and IMAP to be running on your system. For this purpose we'll use GreenMail mail server which can be downloaded from the following link:

[https://greenmail-mail-test.github.io/greenmail/#download](url)

The proper download is here:

<img width="861" height="798" alt="greenmail_download" src="https://github.com/user-attachments/assets/8b57e3a6-f0ae-48fc-8360-bb88583c64bb" />

Once you have the .jar file just execute it with the following command:

`
java -Dgreenmail.setup.test.all -Dgreenmail.users=joona:1234,tomi:4321 -jar greenmail-standalone-2.1.5.jar
`

This sets up a mail server with two users joona and tomi with passwords 1234 and 4321 respectively.

Now just run the Android Application (it's `project/EmailClient`) and log in with one of the user accounts to start sending / receiving mail.

# Demo video

Here is a link to a video demonstration of the app:

[Demo video](https://lut-my.sharepoint.com/:v:/g/personal/joona_niemenmaa_student_lut_fi/EbaCZsQ8LElNsouYyxGCPm0BKy4NNB6a6oaN7RLEDCHy1g?e=2tkOei&nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJTdHJlYW1XZWJBcHAiLCJyZWZlcnJhbFZpZXciOiJTaGFyZURpYWxvZy1MaW5rIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXcifX0%3D)

You need LUT credentials to watch it.
