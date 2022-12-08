import math
pi = 3.142857
m = 8
n = 8
 
# Function to find discrete cosine transform and print it
def dctTransform(matrix,qtable):
 
    # dct will store the discrete cosine transform
    dct = []
    for i in range(m):
        dct.append([None for _ in range(n)])
 
    for u in range(m):
        for v in range(n):
 
            # ci and cj depends on frequency as well as
            # number of row and columns of specified matrix
            if (u == 0):
                ci = 1 / (math.sqrt(2))
            else:
                ci = 1
            if (v == 0):
                cj = 1 / (math.sqrt(2)) 
            else:
                cj = 1
                
            # if (u == 0 & v==0):
            #     ci = 1 / (math.sqrt(2))
            #     cj = 1 / (math.sqrt(2)) 
            # else:
            #     ci = 1
            #     cj = 1
            
 
            # sum will temporarily store the sum of
            # cosine signals
            sum = 0
            for x in range(m):
                for y in range(n):
 
                    dct1 = matrix[x][y] * math.cos((2 * x + 1) * u * math.pi / (
                        16)) * math.cos((2 * y + 1) * v * math.pi / (16))
                    sum = sum + dct1
 
            dct[u][v] = (1/4) * ci * cj * sum
 
    for i in range(m):
        for j in range(n):
            print(round(dct[i][j],3), end="\t")
        print()
    print("----")   
    for i in range(m):
        for j in range(n):
            print(round(dct[i][j]), end="\t")
        print()
    print("----")
    for i in range(m):
        for j in range(n):
            print(round(dct[i][j]/qtable[i][j]), end="\t")
        print()
    print("----")
    print(round(1.5))
# Driver code
matrix = [[139, 144, 149, 153, 155, 155, 155, 155],
          [144, 151, 153, 156, 159, 156, 156, 156],
          [150, 155, 160, 163, 158, 156, 156, 156],
          [159, 161, 162, 160, 160, 159, 159, 159],
          [159, 160, 161, 162, 162, 155, 155, 155],
          [161, 161, 161, 161, 160, 157, 157, 157],
          [162, 162, 161, 163, 162, 157, 157, 157],
          [162, 162, 161, 161, 163, 158, 158, 158]]
          
matrix_test = [[178,187,183,175,178,177,150,183],
          [191,174,171,182,176,171,170,188],
          [199,153,128,177,171,167,173,183],
          [195,178,158,167,167,165,166,177],
          [190,186,158,155,159,164,158,178],
          [194,184,137,148,157,158,150,173],
          [200,194,148,151,161,155,148,167],
          [200,195,175,159,159,152,156,154]]
          
matrix_hw = [[188,180,155,149,179,116,86,96],
          [168,179,168,174,180,111,86,95],
          [150,166,175,189,165,101,88,97],
          [163,165,179,184,135,90,91,96],
          [170,180,178,144,102,87,91,98],
          [175,174,141,104,85,83,88,96],
          [153,134,105,82,83,87,92,96],
          [117,104,86,80,86,90,92,103]]
          
matrix_test2 = [[49,61,69,61,78,89,100,112],
          [68,60,51,42,62,69,80,89],
          [90,81,58,49,69,72,68,69],
          [100,91,79,72,69,68,59,58],
          [111,100,101,91,82,71,59,49],
          [131,119,120,102,90,90,81,59],
          [148,140,129,99,92,78,59,39],
          [151,140,142,119,98,90,72,39]]   
          
qtable = [[16,11,10,16,24,40,51,61],
          [12,12,14,19,26,58,60,55],
          [14,13,16,24,40,57,69,56],
          [14,17,22,29,51,87,80,62],
          [18,22,37,56,68,109,103,77],
          [24,35,55,64,81,104,113,92],
          [49,64,78,87,103,121,120,101],
          [72,92,95,98,112,100,103,99]]        
dctTransform(matrix_hw,qtable)
a = "1101010100101110110100100100000010110000010001100111011100011100100000101100110111101011010"
print(len(a))