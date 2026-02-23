(module rps (game))

(def game 
   (lambda (guess)
      (def ai-roll (list/first (roll '1d3')))
      (def ai-guess (cond 
         ((isEqual 1 ai-roll) 'rock'    )
         ((isEqual 2 ai-roll) 'paper'   )
         ((isEqual 3 ai-roll) 'scissors')
         (true                'panic'   )
      ))
      
      (def game-result (cond
         ((isEqual guess ai-guess)            'Tie.' )   
         ((and (isEqual guess    'rock') 
               (isEqual ai-guess 'scissors')) 'Win!')
         ((and (isEqual guess    'paper')
               (isEqual ai-guess 'rock'))     'Win!')
         ((and (isEqual guess    'scissors') 
               (isEqual ai-guess 'paper'))    'Win!')             
         (true                                'Loss :(')
      ))
      
      (text/concat 
         'You ' game-result ' '
         'Your guess: ' guess 
         ' AI guess: ' ai-guess
      )
))
