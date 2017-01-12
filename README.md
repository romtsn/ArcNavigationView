# ArcNavigationView
NavigationView from android support library with curved edge

<img src="https://raw.githubusercontent.com/rom4ek/ArcNavigationView/master/media/crop_inside.png" width="303"> 	<img src="https://raw.githubusercontent.com/rom4ek/ArcNavigationView/master/media/crop_outside.png" width="303">

# Usage

```xml
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">
    
    ...
    
    <com.rom4ek.arcnavigationview.ArcNavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@android:color/white"
        android:fitsSystemWindows="true"
        app:itemBackground="@android:color/white"
        app:headerLayout="@layout/nav_header_main"
        app:menu="@menu/activity_main_drawer"
        app:arc_cropDirection="cropOutside|cropInside"
        app:arc_width="72dp"/>
</android.support.v4.widget.DrawerLayout>
```

# Sample

##Crop Outside

```xml
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">
    
    ...
    
    <com.rom4ek.arcnavigationview.ArcNavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@android:color/white"
        android:fitsSystemWindows="true"
        app:itemBackground="@android:color/white"
        app:headerLayout="@layout/nav_header_main"
        app:menu="@menu/activity_main_drawer"
        app:arc_cropDirection="cropOutside"
        app:arc_width="72dp"/>
</android.support.v4.widget.DrawerLayout>
```

<img src="https://raw.githubusercontent.com/rom4ek/ArcNavigationView/master/media/crop_outside.png" width="303">


##Crop Inside

```xml
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">
    
    ...
    
    <com.rom4ek.arcnavigationview.ArcNavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@android:color/white"
        android:fitsSystemWindows="true"
        app:itemBackground="@android:color/white"
        app:headerLayout="@layout/nav_header_main"
        app:menu="@menu/activity_main_drawer"
        app:arc_cropDirection="cropInside"
        app:arc_width="72dp"/>
</android.support.v4.widget.DrawerLayout>
```

<img src="https://raw.githubusercontent.com/rom4ek/ArcNavigationView/master/media/crop_inside.png" width="303">


# Additionally

```ArcNavigationView``` also supports end|right gravity mode for displaying it on the right side of the screen. To prevent child views from cutting I recommend to support right-to-left direction. For that you need:

1. Don't forget to support right-to-left mode by adding ```android:supportsRtl="true"``` inside your ```<application/>``` tag in ```AndroidManifest.xml```.
2. Add ```android:layoutDirection="rtl"``` to ```ArcNavigationView```.

You can look how to implement this more closely in the [sample app](https://github.com/rom4ek/ArcNavigationView/tree/master/app)

## TODO

* Implement child views re-layout to prevent them from cutting, while using end|right gravity mode with left-to-right direction.

Acknowledgements
--------

Thanks to [Florent Champigny](https://github.com/florent37) for his beautiful project [ArcLayout](https://github.com/florent37/ArcLayout). I've created this project based on his code with some adjustments.


License
--------

    Copyright 2016 florent37, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
