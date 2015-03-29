1. Download and install eclipse
2. Download java SDK
3. Install android SDK: do the steps here http://developer.android.com/sdk/installing/installing-adt.html 
4. Install eGit: same as point 3 but with URL http://download.eclipse.org/egit/updates 
5. In eclipse select File > Import.. and point to Git > Projects from Git > Clone URI. Fill credentials and repository https://github.com/glugtenberg/mmmiinfo 
6. Download OpenCV here: http://sourceforge.net/projects/opencvlibrary/files/opencv-android/2.4.10/OpenCV-2.4.10-android-sdk.zip/download
7. Unpack the zip somewhere, and in Eclipse: File > Import > existing project into workspace > point to main directory of OpenCv
8. Edit mmmiinfo_2 project settings. Go to Android > Library and Add.. Then point to the OpenCV project.