// Agent attacker_mid in project krislet_player

/* Initial beliefs and rules */

//game setup
boundary(10,-50,-20,20).    //x1,x2,y1,y2   field is -52.54 , 52.54, -34, 34
position(10,0). //left midd start ::::: pos (10,0) for right
net(-52,0). //net position x,y
before_kick_off.


/* Plans */

+before_kick_off <- !set_up.
+goal_l <- !set_up.
+goal_r <- !set_up.

+!set_up <- move(10,0); -before_kick_off. //; updat_pos(-10,0).

+kick_off_l <- !defence.
+kick_off_r <- !attack.

//+ball_in_my_side : play_on <- !defence.  //if the ball is in my zone i defend
//+~ball_in_my_side : play_on <- !attack.    // if the ball is in enemy zone, attack

+play_on <- !attack.

+!attack : ball_not_in_view <- turn_to_ball; -ball_in_view_far; -ball_in_view_close. //!attack.
+!attack : ball_in_view_far <- dash_to_ball; -ball_in_view_close; -ball_not_in_view. //!attack.
+!attack : ball_in_view_close <- -ball_not_in_view; -ball_in_view_far; !pass_kick_dribble. //!attack.
//default for attack
+!attack <- turn_to_ball.

//agent decides if a player should pass, kick or dribble
+!pass_kick_dribble : kick_off_l <- kick_to_goal.
+!pass_kick_dribble : net_close <- kick_to_goal.
+!pass_kick_dribble : net_far <- dribble; -net_close.
+!pass_kick_dribble : cant_advance <- pass.
+!pass_kick_dribble : cant_see_net <- !turn_to_g.
//default for +!pass_kick_dribble
+!pass_kick_dribble <- !attack.

+!turn_to_g : cant_see_net <- turn_to_goal; !turn_to_g.
+!turn_to_g : net_close <- -cant_see_net; !pass_kick_dribble.
+!turn_to_g : net_far <- -cant_see_net; !pass_kick_dribble.
+!turn_to_g <- !turn_to_g.
//defence
+!defence <- !wait_for_pass; move_too(0,0). //middle of the field
//wait for pass
+!wait_for_pass : ball_not_in_view <- turn_to_ball.
+!wait_for_pass : ball_in_view_close <- dribble.
+!wait_for_pass <- turn_to_ball.