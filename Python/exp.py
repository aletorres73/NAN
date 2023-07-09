# -*- coding: utf-8 -*-
"""
Created on Wed Apr  6 10:10:44 2022

@author: atorres
"""

import numpy as np
import matplotlib.pyplot as plt

x  = np.linspace(-2*np.pi, 2*np.pi, 100)
#xx = x + 1j* x [:, np.newaxis] 
out= np.exp(x)

plt.plot(x, out)
plt.show()

# plt.subplot(121)
# plt.imshow(np.abs(out), cmap = 'gray', extent= [-2*np.pi, 2*np.pi,-2*np.pi ,2*np.pi])
# plt.title('Magnitude of exp(x)')

# plt.subplot(122)
# plt.imshow(np.angle(out),cmap='hsv', extent=[-2*np.pi, 2*np.pi, -2*np.pi, 2*np.pi])
# plt.title('Phase (angle) of exp(x)')

# plt.show()
