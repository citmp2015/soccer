import plotly.plotly as py
import plotly.graph_objs as go
import os.path
import sys
import ast

print "Generate heatmap out of flink output data. Specify input file: "

if (len(sys.argv) < 2) and not os.path.isfile(sys.argv[1]):
    print "You have not specified an input file."
    sys.exit(0)

with open(sys.argv[1]) as f:
    title = f.readline()
    content = [ast.literal_eval(line) for line in f.readlines()]

print content

layout = go.Layout(
    title=title,
    font=go.Font(family="Droid Sans, sans-serif",),
    xaxis=go.XAxis(
        title='X sectors',  # x-axis title
        showgrid=True,
    ),
    yaxis=go.YAxis(
        title='Y sectors', # y-axis title
        autorange='reversed',  # (!) reverse tick ordering
        showgrid=True,
    ),
)

data = [
    go.Heatmap(
        z=content,
        x=['1', '2', '3', '4', '5', '6', '7'],
        y=['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13'],
        colorscale='YIOrRd',
        reversescale=True,
    )
]

fig = go.Figure(data=data, layout=layout)

plot_url = py.plot(fig, filename=title)
