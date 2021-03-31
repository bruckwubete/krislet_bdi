
/*

Very simple vacuum cleaner agent in a world that has only four locations.

Perceptions:
. dirty: the current location has dirty
. clean: the current location is clean
. pos(X): the agent position is X (0 < X < 5).

Actions:
. suck: clean the current location
. left, right, up, down: move the agent

*/

// TODO: the code of the agent
+ball_not_in_view <- turn_to_ball.
+ball_in_view_far <- dash_to_ball.
+ball_in_view_close_goal_not_in_view <- turn_to_goal.
+ball_in_view_close_goal_in_view <- kick_to_gaol.
