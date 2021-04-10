// Agent attacker_mid in project krislet_player

/* Initial beliefs and rules */

//game setup
joining_the_game.


/* Plans */

+joining_the_game <- !join.
+!join : joining_the_game <- -joining_the_game; +before_kick_off; join_team(2,left); !set_up.
+!join <- !set_up.


+goal_l <- !set_up.
+goal_r <- !set_up.

+!set_up <- move(2,-36,-20); -before_kick_off.

+kick_off_l <- +attacking; !defence.
+kick_off_r <- +defence; !defence.

+play_on <- !defence.

+!defence : ball_not_in_range <- find_center(2); !defence.
+!defence : ball_in_range <- !attack.
+!defence <- !defence.

+!attack : ball_not_in_view <- turn_to_ball(2). //!attack.
+!attack : ball_in_view_far <- dash(2). //!attack.
+!attack : ball_in_view_close <- !turn_to_g. //!attack.
+!attack <- !attack.

+!turn_to_g : cant_see_net <- turn_to_goal(2); !turn_to_g.
+!turn_to_g : net_close <- !kick_away.
+!turn_to_g : net_far <- !kick_away.
+!turn_to_g <- !turn_to_g.

+!kick_away : cant_see_net <- !turn_to_g.
+!kick_away : net_far <- kick_to_goal(2); +cant_see_flag; !return_to_position.
+!kick_away <- !kick_away.

+!return_to_position : cant_see_flag <- find_flag(2); !return_to_position.
+!return_to_position : can_see_flag <- +station_flag_far; !move_to_flag.
+!return_to_position <- !return_to_position.

+!move_to_flag : station_flag_far <- dash(2); !move_to_flag.
+!move_to_flag : at_station_flag <- -station_flag_far; !defence.
+!move_to_flag <- !move_to_flag.