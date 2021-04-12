// Agent striker in project krislet_player

/* Initial beliefs and rules */
!join_the_game.

+!join_the_game <- join_team(X); init_x(X); init_y(Y); !spwan.
+goal_l <- .drop_all_desires; !spwan.
+goal_r <- .drop_all_desires; !spwan.
+goal_kick_r <- .drop_all_desires; !spwan.
+goal_kick_l <- .drop_all_desires; !spwan.

+!spwan <- .wait(team(T) & (T == left | T == right));
           .wait(1000);
           .wait(init_x(X));
           .wait(init_y(Y));
           move(X, Y);
           !attack.

+!attack : team(T) <- !find_ball;
                       if(T == left) {
                          .wait(kick_off_l | free_kick_l | play_on);
                       } elif(T == right) {
                         .wait(kick_off_r | free_kick_r | play_on);
                       } else {
                          .print("team not found");
                       }
                       !dash_towards_ball;
                       if(T == left) {
                          !find_goal("r");
                       } elif(T == right) {
                          !find_goal("l");
                       }
                       if(T == left) {
                         !strike_to_goal("r");
                       } elif(T == right) {
                         !strike_to_goal("l");
                       }
                       !attack.

+!find_ball : direction("ball", "", X) & (X > 30.0 | X < -30.0) <- turn(X); !find_ball.
+!find_ball : not(distance("ball", "", X)) <- turn(100); !find_ball.
+!find_ball : true <- .print("done, found ball!").

+!dash_towards_ball : direction("ball", "", X) & (X > 2.0 | X < -2.0) <- turn(X); !dash_towards_ball.
+!dash_towards_ball
    : (distance("ball", "", X) & X > 1.0) & (direction("ball", "", Y) & (Y >= -2.0 & Y <= 2.0))
        <-  .drop_intention(find_ball);
            dash(X); !dash_towards_ball.
+!dash_towards_ball : true <- .print("done, reached ball!").

+!find_goal(G) : direction("goal", G, X) & (X > 10.0 | X < -10.0) <-  .printf("trying to find goal %s", G); turn(X); !find_goal(G).
+!find_goal(G) : not(distance("goal", G, X)) <- .printf("trying to find goal %s", G); .drop_intention(dash_towards_ball); turn(100); !find_goal(G).
+!find_goal(G) : true <- .printf("done, found goal %s", G).

+!strike_to_goal(G) : distance("goal", G, X) & direction("goal", G, Y) <-
                    if(X < 10.0) {
                        kick(10, Y); .printf("done, kicked to goal %s", G);
                    } else {
                        kick(0.5, Y); .printf("done, kicked to goal %s", G);
                    }
                    !dash_towards_ball.

