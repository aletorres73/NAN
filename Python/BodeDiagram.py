# -*- coding: utf-8 -*-
"""
Created on Wed Apr  6 13:36:53 2022

@author: atorres
"""

from scipy import signal
import matplotlib.pyplot as plt

tf          = signal.TransferFunction([2],[2,5])
r,mag,phase = signal.bode(tf)

plt.Figure()
plt.subplot(211)
plt.semilogx(r, mag)
plt.grid()
plt.title("Magnitud")

plt.Figure()
plt.subplot(212)
plt.semilogx(r, phase)
plt.grid()
plt.title("Fase")
plt.show()