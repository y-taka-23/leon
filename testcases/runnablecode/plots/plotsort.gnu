set terminal jpeg

set output "sortorbVsActual.jpg"

set key left top

set xlabel "n"
set ylabel "time"
set title "Plot for sort, Orb vs Runnable code"

set grid ytics lt 0 lw 1 lc rgb "#bbbbbb"
set grid xtics lt 0 lw 1 lc rgb "#bbbbbb"

plot \
"<(sed -n '30,40p' sort-runnable.data)" using 1:2 t'runtime' with linespoints, \
"<(sed -n '30,40p' sort-orb1.data)" using 1:2 t'orb1' with linespoints, \
"<(sed -n '30,40p' sort-orb2.data)" using 1:2 t'orb2' with linespoints,
