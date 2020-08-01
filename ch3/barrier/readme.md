# purpose

- wait for a bunch of threads to complete their work before continuing
  - synchronization point

- rendezvous for more than two people

# example

- doing a large computation and must wait for all computation is done before
  doing the aggregation step

# 3.6.4

- issue:
  - threads might be stuck at line and when they wake up they all get
    the if condition, i.e. too many signals generated


