# SpeechToText

A Java speech to text application that uses https://app.assemblyai.com/ API to convert a specified audio file to text. The audio file must be hosted online.

The executable .jar file is complete and ready to use. It is located under the artifacts. Usage is as follows:

``java -jar SpeechToTextAPI.jar <audio_url>``

The source code is not complete due to secrets (authentication key for assemblyai.com). In order to make it fully work, create a class Authentication.java that contains a string value of your personal authentication key.
