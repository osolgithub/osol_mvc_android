# README
<!-- Replace ^[^#]([\r\n]*) with blank to make a template. In note pad you can also use `negative lookahead` ^(?!") -->


## Author

Sreekanth Dayanand

### Contributors

## Synopsis

This is android app skelton to sync with an OSOS MVC Instance generated via [OSOL MVC Maker](https://hariyom.bitbucket.io/MVCMaker/html/md_readme.html)

## Description
One or two paragraph of project Description goes here

## Prerequisites
1. **List of all dependensies required**
2. **What all should be installed as prerequisites**
3. **How to prerequisites**

## Installation

### 1. Rename Project
Step By step installation guide
1. copy to a new folder
2. rename source code folders 'com.example.osolmvcandroid' to 'com.yourdomain.osolmvcandroid'
3. to rename inside Android studio use [this tip](https://stackoverflow.com/a/29092698). ie `Compact Empty Middle Packages` in Project panel gear icon.
4. Replace all in project `(Ctrl+shift + R)` 'com.example.osolmvcandroid' to 'com.yourdomain.osolmvcandroid'
5. to confirm change package name with refactor
6. change `rootProject.name` in `settings.gradle`
### 2. Manifest file

There are three properties you need to change in the manifest file:

1. The package attribute of the <manifest> tag
2. The android:label attribute of the <application> tag
3. The android:name attribute of the <activity> tag for the MainActivity(if required)


### 3. Set values for the project
 **in values/strings**@n
 
 1. change `<string name="app_name">OSOL MVC Android</string>`
 2. change `<string name="google_login_web_client_id">`
 3. change 
```
<string name="facebook_app_id">
<string name="fb_login_protocol_scheme">
<string name="facebook_client_token">
```

** in mvc.Config.java**@n
1. change `private String homePage`
 
 ### 4. Gradle Sync
1. File &gt;&gt; Reload from Disk
2. Gradle sync
3. if it still doesnt work  select Build >> Rebuild Project
 



\warning 

1. Google signin Error : <b>Warning</b>:  count(): Parameter must be an array or an object that implements Countable in <b>vendor/guzzlehttp/guzzle/src/Handler/CurlFactory.php</b> on line <b>67</b><br />
[Solution](https://stackoverflow.com/a/55238965)
change `count($this->handles)` to `count((is_countable($this->handles)?$this->handles:[]))`

## Extending / Installing Addons
1. use `mvc.Helper` path to hold logic


## Porting to a different server

Follow the steps mentioned above as needed.

## Contributing
Issue Tracker: github.com/project/issues

## License / Copyright Info
Licence Information

## Citation
1. How this software can be cited
2. DOI(Digital Object Identifier) link/image

## Contact
Email addesses or Contact us links

## Refernces

[copy android project to make new one](https://sebhastian.com/android-studio-copy-project/)\n
[rename sub folders within android studio](https://stackoverflow.com/a/29092698)
