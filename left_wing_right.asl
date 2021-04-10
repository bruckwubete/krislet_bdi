// Agent left_wing_right in project krislet_player
boundary(-10,50,-20,20).
position(-10,0).
joining_the_game.


/* Plans */

+joining_the_game <- !join.
+!join : joining_the_game<- -joining_the_game; +before_kick_off; join_team(1,left); !set_up.
+!join <- !set_up.


+goal_l <- !set_up.
+goal_r <- !set_up.

+!set_up <- print(position). //; updat_pos(-10,0).