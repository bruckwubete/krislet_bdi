// Agent attacker_mid in project krislet_player

/* Initial beliefs and rules */

//game setup
joining_the_game.


/* Plans */

+joining_the_game <- !join.
+!join : joining_the_game <- -joining_the_game; +before_kick_off; join_team(4,right); !set_up.
+!join <- !set_up.


+goal_l <- !set_up.
+goal_r <- !set_up.

+!set_up <- move(-36,20); -before_kick_off.

+kick_off_l <- !defence.
+kick_off_r <- !defence.

+play_on <- !defence.

+!defence : not(viz("ball", "")) <- turn_to_ball. //!attack.
+!defence : viz("ball", "") & distance("ball", "", X) & X > 25.0 <- turn_to_ball. //!attack.
+!defence : viz("ball", "") & distance("ball", "", X) & X <= 25.0 <- !chase_ball. //!attack.
//default for attack
+!defence <- turn_to_ball; !defence.

+!chase_ball : not(viz("ball", "")) <- turn_to_ball.
+!chase_ball : viz("ball", "") & distance("ball", "", X) & X > 25.0 & direction("ball", "", Y) & Y > 2.0 <- !return_to_flag. // !chase_ball.
+!chase_ball : viz("ball", "") & distance("ball", "", X) & X > 1.0 & direction("ball", "", Y) & Y > 2.0 <- turn_to_ball; !chase_ball.
+!chase_ball : viz("ball", "") & distance("ball", "", X) & X > 1.0 & direction("ball", "", Y) & Y <= 2.0 <- dash; !chase_ball.
+!chase_ball : viz("ball", "") & distance("ball", "", X) & X <= 1.0 <- !pass_kick_dribble.
+!chase_ball <- turn_to_ball; !chase_ball.

//agent decides if a player should pass, kick or dribble
+!pass_kick_dribble : viz("goal", "l") <- kick_to_goal(l); !return_to_flag.
+!pass_kick_dribble : not(viz("goal", "l")) <- !turn_to_g.
//default for +!pass_kick_dribble
+!pass_kick_dribble <- turn_to_ball; !defence.

+!turn_to_g : not(viz("goal", "l")) <- turn_to_goal(l); !turn_to_g.
+!turn_to_g : viz("goal", "l") <- !pass_kick_dribble.
+!turn_to_g <- turn_to_ball; !turn_to_g.

+!return_to_flag : not(viz("flag", "prt")) & goal_l <- !set_up.
+!return_to_flag : not(viz("flag", "prt")) & goal_r <- !set_up.
+!return_to_flag : not(viz("flag", "prt")) <- turn_to_flag(prt); !return_to_flag.
+!return_to_flag : viz("flag", "prt") & distance("flag", "prt", X) & X > 1.0 & direction("flag", "prt", Y) & Y > 5.0 <- turn_to_flag(prt); !return_to_flag.
+!return_to_flag : viz("flag", "prt") & distance("flag", "prt", X) & X > 1.0 & direction("flag", "prt", Y) & Y <= 5.0 <- dash; !return_to_flag.
+!return_to_flag : viz("flag", "prt") & distance("flag", "prt", X) & X <= 1.0 <- !defence.
+!return_to_flag <- !return_to_flag.

//+!attack <- !defence. //temp 