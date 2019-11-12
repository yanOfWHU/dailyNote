# vim oom_score.sh
#!/bin/bash
printf "%2s %5s %s\n" score pid detail
for proc in $(find /proc -maxdepth 1 -regex '/proc/[0-9]+');do
    printf "%2d %5d %s\n"\
          "$(cat $proc/oom_score)"\
          "$(basename $proc)"\
          "$(cat $proc/cmdline | tr '\0' ' ' | head -c 50)"
done 2>/dev/null | sort -nr | head -n 10
