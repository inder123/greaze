## Installing client projects only ##
  * mkdir ~/ws; cd ~/ws
  * hg clone https://code.google.com/p/greaze/
  * cd ~/ws/greaze/definition
  * mvn eclipse:eclipse install
  * cd ../client
  * mvn eclipse:eclipse install
  * In Eclipse, Select File | Import | General | Existing Projects into Workspace. Select ~/ws/greaze/definition and ~/ws/greaze/client to load these as projects in Eclipse.
  * If you have m2eclipse installed, right click on each of the projects, select Configure | Convert to Maven Project

For installing the server project, repeat the above steps for ~/ws/greaze/server directory.

For installing the end2end project, repeat the above steps for ~/ws/greaze/end2end directory.