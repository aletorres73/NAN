# -*- coding: utf-8 -*-
"""
Created on Wed Apr  6 09:53:57 2022

@author: atorres
"""

import numpy as np
import matplotlib.pyplot as plt

x = np.arange(0 ,4*np.pi, 0.1)
y = np.sin(x)

plt.subplot(3,1,1)
plt.plot(x ,y)
plt.subplot(3,1,2)
plt.plot(x,2*y)
plt.subplot(3,1,3)
plt.plot(x*0.5,np.cos(x))
plt.show()
