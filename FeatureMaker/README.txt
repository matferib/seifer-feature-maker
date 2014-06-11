This program is used to create / edit / generate tdf files for CATE. A tdf file defines features location, using quadrant 1 (meaning 0,0 is the lower left). Right now, 2 types of features can be created: RIVER and ROAD. More can be easily added if needs arise.

To run this program on linux, execute featureMaker.sh. On windows, use the bat file or double click the jar file inside the dist directory.

When opened, program will show a blank window and ask for a map image. After map is loaded, one can:

- Load a features file(tdf): L key
- Save current features to a tdf file: S key
- Create a new feature: INSERT key

To edit existing features, select waypoints with mouse, drag and drop them to move. 
- To delete a waypoint, hit DELETE
- To create a new waypoint, after the one selected, hit INSERT.
- To delete an entire feature, hit shift DELETE.
- To move an entire feature, use right mouse button to drag and drop.
- To toggle feature type, hit T.

Thats it, as simple as that. No fancy UI, though.

As always, have fun,
Seifer out
