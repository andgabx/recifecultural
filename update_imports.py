import os
import re

directories = ['dominio-agenda', 'dominio-espaco', 'dominio-artista', 'dominio-compartilhado', 'dominio-financeiro', 'dominio-ingressos', 'dominio-patrocinio', 'aplicacao', 'apresentacao-backend']

for d in directories:
    for root, dirs, files in os.walk(d):
        for file in files:
            if file.endswith('.java'):
                filepath = os.path.join(root, file)
                with open(filepath, 'r') as f:
                    content = f.read()
                
                new_content = content.replace('recifecultural.dominio.agenda.espaco', 'recifecultural.dominio.espaco.espaco')
                new_content = new_content.replace('recifecultural.dominio.agenda.setor', 'recifecultural.dominio.espaco.setor')
                
                if new_content != content:
                    with open(filepath, 'w') as f:
                        f.write(new_content)
                    print(f"Updated {filepath}")
