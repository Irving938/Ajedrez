    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     */

    package com.mycompany.ajedrez;

    /**
     *
     * @author irvin
     */

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.*;
    import java.util.Map;
    import java.util.HashMap;
    import java.util.ArrayList;
    import java.io.IOException;
    import javax.imageio.ImageIO;

    public class AppAjedrez {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                VentanaAjedrez ventana = new VentanaAjedrez();
                ventana.setVisible(true);
            });
        }
    }


    class VentanaAjedrez extends JFrame {
        public VentanaAjedrez() {
            setTitle("Ajedrez - Proyecto NetBeans");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(720, 760);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            ModeloTablero modelo = new ModeloTablero();
            PanelTablero panelTablero = new PanelTablero(modelo);
            add(panelTablero, BorderLayout.CENTER);

            JLabel etiquetaEstado = new JLabel("Turno: BLANCAS");
            etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
            add(etiquetaEstado, BorderLayout.SOUTH);

            modelo.setEtiquetaEstado(etiquetaEstado);
        }
    }

 
    enum ColorPieza { BLANCO, NEGRO }


    abstract class Pieza {
        ColorPieza color;
        char abreviatura;

        public Pieza(ColorPieza color, char abreviatura){
            this.color = color;
            this.abreviatura = abreviatura;
        }

        public ColorPieza getColor() { return color; }

        public abstract java.util.List<Jugada> movimientosLegales(ModeloTablero tablero, int f, int c);

        public String toString() { return (color==ColorPieza.BLANCO?"W":"B")+abreviatura; }

        public abstract String getRutaImagen();
    }

   
    class Rey extends Pieza {
        public Rey(ColorPieza color){ super(color,'R'); }

        public java.util.List<Jugada> movimientosLegales(ModeloTablero tablero, int f, int c){
            java.util.List<Jugada> res = new ArrayList<>();
            for(int df=-1; df<=1; df++)
                for(int dc=-1; dc<=1; dc++){
                    if(df==0 && dc==0) continue;
                    int nf=f+df, nc=c+dc;
                    if(tablero.estaDentro(nf,nc) && !tablero.mismaColor(nf,nc,color))
                        res.add(new Jugada(f,c,nf,nc));
                }
            if(!tablero.haMovido(f,c)){
                if(tablero.puedeEnroqueCorto(color)) res.add(new Jugada(f,c,f,c+2));
                if(tablero.puedeEnroqueLargo(color)) res.add(new Jugada(f,c,f,c-2));
            }
            return res;
        }

        public String getRutaImagen() {
            return "/imagenes/" + (color==ColorPieza.BLANCO?"rey_blanco.png":"rey_negro.png");
        }
    }


    class Reina extends Pieza {
        public Reina(ColorPieza color){ super(color,'Q'); }

        public java.util.List<Jugada> movimientosLegales(ModeloTablero tablero, int f, int c){
            java.util.List<Jugada> res = new ArrayList<>();
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,1,0,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,-1,0,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,0,1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,0,-1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,1,1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,1,-1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,-1,1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,-1,-1,color));
            return res;
        }

        public String getRutaImagen() {
            return "/imagenes/" + (color==ColorPieza.BLANCO?"reina_blanca.png":"reina_negra.png");
        }
    }

 
    class Torre extends Pieza {
        public Torre(ColorPieza color){ super(color,'T'); }

        public java.util.List<Jugada> movimientosLegales(ModeloTablero tablero, int f, int c){
            java.util.List<Jugada> res = new ArrayList<>();
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,1,0,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,-1,0,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,0,1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,0,-1,color));
            return res;
        }

        public String getRutaImagen() {
            return "/imagenes/" + (color==ColorPieza.BLANCO?"torre_blanca.png":"torre_negra.png");
        }
    }


    class Alfil extends Pieza {
        public Alfil(ColorPieza color){ super(color,'A'); }

        public java.util.List<Jugada> movimientosLegales(ModeloTablero tablero, int f, int c){
            java.util.List<Jugada> res = new ArrayList<>();
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,1,1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,1,-1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,-1,1,color));
            res.addAll(ModeloTablero.movimientosEnLinea(tablero,f,c,-1,-1,color));
            return res;
        }

        public String getRutaImagen() {
            return "/imagenes/" + (color==ColorPieza.BLANCO?"alfil_blanco.png":"alfil_negro.png");
        }
    }


    class Caballo extends Pieza {
        public Caballo(ColorPieza color){ super(color,'C'); }

        public java.util.List<Jugada> movimientosLegales(ModeloTablero tablero, int f, int c){
            java.util.List<Jugada> res = new ArrayList<>();
            int[] df = {2,2,-2,-2,1,1,-1,-1};
            int[] dc = {1,-1,1,-1,2,-2,2,-2};
            for(int i=0;i<df.length;i++){
                int nf=f+df[i], nc=c+dc[i];
                if(tablero.estaDentro(nf,nc) && !tablero.mismaColor(nf,nc,color))
                    res.add(new Jugada(f,c,nf,nc));
            }
            return res;
        }

        public String getRutaImagen() {
            return "/imagenes/" + (color==ColorPieza.BLANCO?"caballo_blanco.png":"caballo_negro.png");
        }
    }


    class Peon extends Pieza {
        public Peon(ColorPieza color){ super(color,'P'); }

        public java.util.List<Jugada> movimientosLegales(ModeloTablero tablero, int f, int c){
            java.util.List<Jugada> res = new ArrayList<>();
            int dir = (color==ColorPieza.BLANCO)?-1:1;
            int nf = f + dir;

            if(tablero.estaDentro(nf,c) && tablero.getPieza(nf,c)==null){
                res.add(new Jugada(f,c,nf,c));
                if((color==ColorPieza.BLANCO && f==6)||(color==ColorPieza.NEGRO && f==1)){
                    int nf2=f+2*dir;
                    if(tablero.getPieza(nf2,c)==null) res.add(new Jugada(f,c,nf2,c));
                }
            }

            for(int dc=-1; dc<=1; dc+=2){
                int nc = c+dc;
                if(tablero.estaDentro(nf,nc) && tablero.getPieza(nf,nc)!=null && tablero.getPieza(nf,nc).getColor()!=color)
                    res.add(new Jugada(f,c,nf,nc));
            }

            return res;
        }

        public String getRutaImagen() {
            return "/imagenes/" + (color==ColorPieza.BLANCO?"peon_blanco.png":"peon_negro.png");
        }
    }


    class Jugada {
        int f1,c1,f2,c2;
        public Jugada(int f1,int c1,int f2,int c2){this.f1=f1;this.c1=c1;this.f2=f2;this.c2=c2;}
    }


    class ModeloTablero {
        private Pieza[][] tablero;
        private boolean[][] movido;
        private ColorPieza turno;
        private JLabel etiquetaEstado;

        public ModeloTablero(){
            tablero = new Pieza[8][8];
            movido = new boolean[8][8];
            turno = ColorPieza.BLANCO;
            inicializarTablero();
        }

        public void setEtiquetaEstado(JLabel l){ this.etiquetaEstado=l; actualizarEstado(); }
        public void actualizarEstado(){ if(etiquetaEstado!=null) etiquetaEstado.setText("Turno: "+(turno==ColorPieza.BLANCO?"BLANCAS":"NEGRAS")); }

        public Pieza getPieza(int f,int c){ return tablero[f][c]; }
        public boolean estaDentro(int f,int c){ return f>=0 && f<8 && c>=0 && c<8; }
        public boolean mismaColor(int f,int c, ColorPieza color){ Pieza p=getPieza(f,c); return p!=null && p.getColor()==color; }
        public boolean haMovido(int f,int c){ return movido[f][c]; }

        public ColorPieza getTurno(){ return turno; }


        public static java.util.List<Jugada> movimientosEnLinea(ModeloTablero modelo, int f, int c, int df, int dc, ColorPieza color){
            java.util.List<Jugada> res = new ArrayList<>();
            int nf = f + df, nc = c + dc;
            while(modelo.estaDentro(nf,nc)){
                if(modelo.getPieza(nf,nc)==null) res.add(new Jugada(f,c,nf,nc));
                else {
                    if(modelo.getPieza(nf,nc).getColor()!=color) res.add(new Jugada(f,c,nf,nc));
                    break;
                }
                nf += df; nc += dc;
            }
            return res;
        }


        public boolean puedeEnroqueCorto(ColorPieza color){
            int fila = (color==ColorPieza.BLANCO)?7:0;
            if(!(tablero[fila][4] instanceof Rey) || !(tablero[fila][7] instanceof Torre)) return false;
            if(haMovido(fila,4) || haMovido(fila,7)) return false;
            for(int c=5;c<7;c++) if(tablero[fila][c]!=null) return false;
            return true;
        }

        public boolean puedeEnroqueLargo(ColorPieza color){
            int fila = (color==ColorPieza.BLANCO)?7:0;
            if(!(tablero[fila][4] instanceof Rey) || !(tablero[fila][0] instanceof Torre)) return false;
            if(haMovido(fila,4) || haMovido(fila,0)) return false;
            for(int c=1;c<4;c++) if(tablero[fila][c]!=null) return false;
            return true;
        }


        public boolean moverSiLegal(Jugada j){
            Pieza p = getPieza(j.f1,j.c1);
            if(p==null || p.getColor()!=turno) return false;

            java.util.List<Jugada> legales = p.movimientosLegales(this,j.f1,j.c1);
            boolean encontrado = false;
            for(Jugada jl : legales) if(jl.f2==j.f2 && jl.c2==j.c2) { encontrado=true; break; }
            if(!encontrado) return false;

            Pieza[][] copiaTablero = copiarTablero();
            boolean[][] copiaMovido = copiarMovido();

   
            if(p instanceof Rey && Math.abs(j.c2-j.c1)==2){
                if(j.c2>j.c1){
                    tablero[j.f2][6]=p; tablero[j.f1][j.c1]=null;
                    tablero[j.f2][5]=tablero[j.f2][7]; tablero[j.f2][7]=null;
                    movido[j.f2][6]=true; movido[j.f2][5]=true;
                } else {
                    tablero[j.f2][2]=p; tablero[j.f1][j.c1]=null;
                    tablero[j.f2][3]=tablero[j.f2][0]; tablero[j.f2][0]=null;
                    movido[j.f2][2]=true; movido[j.f2][3]=true;
                }
            } else {
                tablero[j.f2][j.c2]=tablero[j.f1][j.c1];
                tablero[j.f1][j.c1]=null;
                movido[j.f2][j.c2]=true;
            }


            if(tablero[j.f2][j.c2] instanceof Peon){
                if((tablero[j.f2][j.c2].getColor()==ColorPieza.BLANCO && j.f2==0) ||
                   (tablero[j.f2][j.c2].getColor()==ColorPieza.NEGRO && j.f2==7)){
                    Object[] opciones = {"Reina","Torre","Alfil","Caballo"};
                    int seleccion = JOptionPane.showOptionDialog(null,"Elige promoción","Promoción",
                            JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,opciones,opciones[0]);
                    switch(seleccion){
                        case 1: tablero[j.f2][j.c2]= new Torre(tablero[j.f2][j.c2].getColor()); break;
                        case 2: tablero[j.f2][j.c2]= new Alfil(tablero[j.f2][j.c2].getColor()); break;
                        case 3: tablero[j.f2][j.c2]= new Caballo(tablero[j.f2][j.c2].getColor()); break;
                        default: tablero[j.f2][j.c2]= new Reina(tablero[j.f2][j.c2].getColor()); break;
                    }
                }
            }


            if(estaReyEnJaque(turno)){
                tablero=copiaTablero; movido=copiaMovido; return false;
            }

            turno = (turno==ColorPieza.BLANCO)?ColorPieza.NEGRO:ColorPieza.BLANCO;
            actualizarEstado();

            if(esJaqueMate(turno))
                JOptionPane.showMessageDialog(null,"JAQUE MATE. Ganan "+(turno==ColorPieza.BLANCO?"NEGRAS":"BLANCAS"));

            return true;
        }


        public boolean estaReyEnJaque(ColorPieza color){
            int fr=-1, fc=-1;
            for(int i=0;i<8;i++) for(int j=0;j<8;j++)
                if(tablero[i][j] instanceof Rey && tablero[i][j].getColor()==color){ fr=i; fc=j; }
            if(fr==-1) return false;

            for(int i=0;i<8;i++) for(int j=0;j<8;j++){
                Pieza p = tablero[i][j];
                if(p!=null && p.getColor()!=color){
                    for(Jugada jl: p.movimientosLegales(this,i,j))
                        if(jl.f2==fr && jl.c2==fc) return true;
                }
            }
            return false;
        }

        public boolean esJaqueMate(ColorPieza color){
            if(!estaReyEnJaque(color)) return false;
            for(int i=0;i<8;i++) for(int j=0;j<8;j++){
                Pieza p = tablero[i][j];
                if(p!=null && p.getColor()==color){
                    for(Jugada jl: p.movimientosLegales(this,i,j)){
                        Pieza[][] copiaTablero = copiarTablero();
                        boolean[][] copiaMovido = copiarMovido();
                        Pieza capturada = tablero[jl.f2][jl.c2];
                        tablero[jl.f2][jl.c2]=tablero[i][j]; tablero[i][j]=null;
                        if(!estaReyEnJaque(color)){ tablero=copiaTablero; movido=copiaMovido; return false; }
                        tablero=copiaTablero; movido=copiaMovido;
                    }
                }
            }
            return true;
        }


        public Pieza[][] copiarTablero(){
            Pieza[][] copia = new Pieza[8][8];
            for(int i=0;i<8;i++) for(int j=0;j<8;j++) copia[i][j]=tablero[i][j];
            return copia;
        }

        public boolean[][] copiarMovido(){
            boolean[][] copia = new boolean[8][8];
            for(int i=0;i<8;i++) for(int j=0;j<8;j++) copia[i][j]=movido[i][j];
            return copia;
        }


        private void inicializarTablero(){
            for(int i=0;i<8;i++) for(int j=0;j<8;j++) tablero[i][j]=null;
            for(int j=0;j<8;j++){ tablero[6][j]=new Peon(ColorPieza.BLANCO); tablero[1][j]=new Peon(ColorPieza.NEGRO); }
            tablero[7][0]=new Torre(ColorPieza.BLANCO); tablero[7][7]=new Torre(ColorPieza.BLANCO);
            tablero[0][0]=new Torre(ColorPieza.NEGRO); tablero[0][7]=new Torre(ColorPieza.NEGRO);
            tablero[7][1]=new Caballo(ColorPieza.BLANCO); tablero[7][6]=new Caballo(ColorPieza.BLANCO);
            tablero[0][1]=new Caballo(ColorPieza.NEGRO); tablero[0][6]=new Caballo(ColorPieza.NEGRO);
            tablero[7][2]=new Alfil(ColorPieza.BLANCO); tablero[7][5]=new Alfil(ColorPieza.BLANCO);
            tablero[0][2]=new Alfil(ColorPieza.NEGRO); tablero[0][5]=new Alfil(ColorPieza.NEGRO);
            tablero[7][3]=new Reina(ColorPieza.BLANCO); tablero[0][3]=new Reina(ColorPieza.NEGRO);
            tablero[7][4]=new Rey(ColorPieza.BLANCO); tablero[0][4]=new Rey(ColorPieza.NEGRO);
        }
    }


    class PanelTablero extends JPanel {
        private ModeloTablero modelo;
        private int tamañoCasilla = 80;
        private int filaSeleccionada=-1, colSeleccionada=-1;
        private java.util.List<Jugada> resaltadas = new ArrayList<>();
        private Map<String, Image> imagenesPiezas = new HashMap<>();

        public PanelTablero(ModeloTablero modelo){
            this.modelo = modelo;
            setPreferredSize(new Dimension(8*tamañoCasilla,8*tamañoCasilla));
            setBackground(Color.DARK_GRAY);

            cargarImagenes();

            addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e){
                    int c = e.getX()/tamañoCasilla;
                    int f = e.getY()/tamañoCasilla;
                    if(!modelo.estaDentro(f,c)) return;
                    Pieza p = modelo.getPieza(f,c);
                    if(filaSeleccionada==-1){
                        if(p!=null && p.getColor()==modelo.getTurno()){
                            filaSeleccionada=f; colSeleccionada=c;
                            resaltadas = p.movimientosLegales(modelo,f,c);
                            repaint();
                        }
                    } else {
                        modelo.moverSiLegal(new Jugada(filaSeleccionada,colSeleccionada,f,c));
                        filaSeleccionada=-1; colSeleccionada=-1; resaltadas.clear(); repaint();
                    }
                }
            });
        }

        private void cargarImagenes() {
            try {
                String[] nombres = {
                    "rey_blanco", "reina_blanca", "torre_blanca", "alfil_blanco", "caballo_blanco", "peon_blanco",
                    "rey_negro", "reina_negra", "torre_negra", "alfil_negro", "caballo_negro", "peon_negro"
                };
                for (String n : nombres) {
                    Image img = ImageIO.read(getClass().getResource("/imagenes/" + n + ".png"));
                    imagenesPiezas.put(n, img.getScaledInstance(tamañoCasilla, tamañoCasilla, Image.SCALE_SMOOTH));
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error cargando imágenes: " + e.getMessage());
            }
        }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        for(int fila = 0; fila < 8; fila++){
            for(int col = 0; col < 8; col++){
                if((fila + col) % 2 == 0) g.setColor(Color.LIGHT_GRAY);
                else g.setColor(Color.GRAY);
                g.fillRect(col * tamañoCasilla, fila * tamañoCasilla, tamañoCasilla, tamañoCasilla);
            }
        }

        g.setColor(new Color(0, 255, 0, 128));
        for(Jugada j : resaltadas){
            g.fillRect(j.c2 * tamañoCasilla, j.f2 * tamañoCasilla, tamañoCasilla, tamañoCasilla);
        }

        for(int fila = 0; fila < 8; fila++){
            for(int col = 0; col < 8; col++){
                Pieza p = modelo.getPieza(fila, col);
                if(p != null){
                    String key = "";
                    if(p instanceof Rey) key = "rey_" + (p.getColor() == ColorPieza.BLANCO ? "blanco" : "negro");
                    if(p instanceof Reina) key = "reina_" + (p.getColor() == ColorPieza.BLANCO ? "blanca" : "negra");
                    if(p instanceof Torre) key = "torre_" + (p.getColor() == ColorPieza.BLANCO ? "blanca" : "negra");
                    if(p instanceof Alfil) key = "alfil_" + (p.getColor() == ColorPieza.BLANCO ? "blanco" : "negro");
                    if(p instanceof Caballo) key = "caballo_" + (p.getColor() == ColorPieza.BLANCO ? "blanco" : "negro");
                    if(p instanceof Peon) key = "peon_" + (p.getColor() == ColorPieza.BLANCO ? "blanco" : "negro");

                    Image img = imagenesPiezas.get(key);
                    if(img != null){
                        g.drawImage(img, col * tamañoCasilla, fila * tamañoCasilla, tamañoCasilla, tamañoCasilla, this);
                    }
                }
            }
        }
    }
    }
