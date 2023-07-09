# %% pandas 1
import pandas as pd

df = pd.read_csv('https://raw.githubusercontent.com/asalber/manual-python/master/datos/colesterol.csv')

print(df.drop([1, 3]))
