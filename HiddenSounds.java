package hiddensounds;
/* Progetto per il corso di Programmazione per la Musica
    Clerici Giulia 
    matr. 910663
    CdL Informatica
*/
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.event.*;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.RedNoise;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitOscillator;
import com.softsynth.shared.time.TimeStamp;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HiddenSounds extends JFrame{ // implements ActionListener
    final int width = 600; // larghezza del frame  
    final int height = 600;  // altezza del frame
    private String title = "Hidden Sounds - Giulia Clerici"; // dichiarazione del titolo del frame
    public int r = 10; // imposto numero di righe
    public int c = 10; // imposto numero di colonne
    public int rN = 10; // numero di righe, variabile che servirà per la modifica del numero di pannelli  
    public int cN = 10; // numero di colonne, variabile che servirà per la modifica del numero di pannelli 
    public ArrayList<JPanel> panel = new ArrayList<>(); // ArrayList, struttura dinamica contenente i pannelli JPanel
    Color backgroundColor = new Color(252, 219, 241); // colore di sfondo del JFrame
    synthSounds sound = new synthSounds(); // dichiaro una variabile di classe synthSound creata da me per la sintesi sonora
    String s = ""; // stringa vuota che memorizzerà quanto digitato da tastiera
    String t = "twinpeaks"; // stringa che servirà per confrontare i caratteri digitati
    MyKeyboardAdapter adapter; // adapter per gli input da tastiera
            
    public HiddenSounds(){ // costruttore della classe principale HiddenSounds
        setTitle(title); // imposto il titolo del JFrame
        setSize(width, height); // imposto delle misure di larghezza e altezza
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // faccio in modo che l'applicazione venga chiusa alla chiusura della finestra
        setLayout(new GridLayout(r,c)); // imposto come Layout un GridLayout di r righe e c colonne 
        setResizable(true); // imposto a true la possibilità di ridimensionare la finestra
        int n = 0; // dichiaro e inizializzo n, che sarà la variabile che indicherà quale pannello andare a prendere (da 0 a r*c)
        for(int i = 0; i < r; i ++){ // due cicli for annidati per ciclare sulle righe e colonne del layout 
            for(int j = 0; j < c; j++){
                panel.add(new JPanel()); // inizializzo un nuovo JPanel
                MyKeyboardAdapter adapter = new MyKeyboardAdapter(); // dichiaro e inizializzo l'adapter per la tastiera 
                panel.get(n).addKeyListener(adapter); // aggiungo un KeyListener che riceverà gli eventi da tastiera, che verranno gestiti dall'adapter
                panel.get(n).setFocusable(true); // faccio in modo che il pannello panel[n] sia "focusable", possa essere "focalizzato"
                add(panel.get(n)); // aggiungo il pannello al frame 
                panel.get(n).setBackground(backgroundColor); // imposto il colore di sfondo del pannello 
                n++; // avanzo di una posizione per prendere il pannello successivo
            }  
        }
    }
    
    public static void main(String[] args) {
        HiddenSounds frame = new HiddenSounds(); // dichiaro e inizializzo il frame 
        frame.setVisible(true); // rendo visibile la finestra
    }
    
    class MyKeyboardAdapter extends KeyAdapter { // implemento l'adapter per gli eventi legati alla tastiera
        @Override
        public void keyTyped(KeyEvent e){ // faccio l'override del metodo che gestisce la digitazione dei tasti della tastiera
            char carattere = e.getKeyChar(); // recupero il tasto cliccato dall'utente 
            if ( carattere == e.VK_ENTER || carattere == e.VK_SPACE || carattere == e.VK_BACK_SPACE ) { // se vengono cliccati invio, backspace o spazio
                moreJPanels(); // chiamo la funziona morePanels che si occupa di aumentare i pannelli presenti nel JFrame
            }else if(carattere == e.VK_ESCAPE){ // se l'utente clicca il tasto esc
                System.exit(0); // il programma viene terminato
            }else{ // altrimenti, se digito qualsiasi altro carattere
                if(s.length() > t.length()) // se la stringa s ha lunghezza > 9, ovvero ho appena suonato il brano di twin peaks
                    s = ""; // svuoto la stringa
                s += carattere; // aggiungo alla stringa s il carattere appena digitato
                System.out.println(s); // stampo il contenuto della stringa s 
                for(int i = 0; i < s.length(); i++) // ciclo sulla lunghezza della stringa s 
                    if(!(s.charAt(i) == t.charAt(i))) // se non succede che ogni carattere finora contenuto in s corrisponde agli stessi caratteri contenuti nelle stesse posizioni della stringa "twinpeaks"
                        s = ""; // allora svuoto la stringa
                if( s.equals(t) ){ // se la stringa s contiene le lettere "twinpeaks"
                    sound.playSample(); // chiamo la funzione che riproduce il campione audio del brano
                    s = ""; // e poi svuoto la stringa
                }else { // altrimenti
                    int n = (int) (Math.random() * (r * c)); // genero un numero casuale da 0 a r*c, andrà ad indicare quale pannello sarà selezionato per la modifica di colore
                    int[] rgb = new int[3]; // dichiaro array che conterrà i valori RGB (posizionati in ordine, dunque r in posizione 0, g in posizione 1 e b in posizione 2)
                    rgb = pastelColors(); // chiamo il metodo che restituisce un array di valori RGB di colori pastello generati randomicamente
                    panel.get(n).setBackground(new Color(rgb[0], rgb[1], rgb[2])); // posto il colore di background del pannello ad un colore pastello generato randomicamente
                    double[] hsb = new double[3]; // dichiaro un array che conterrà i valori HSB, ordinati
                    hsb = rgbToHsb(rgb); // chiamo il metodo che converte i valori RGB in HSB
                    sound.play(rgb, hsb); // genero un suono condizionato dai valori RGB e HSB 
                }
            }
        }    
        
    }
    
        public void moreJPanels(){ // funzione che si occupa di aumentare il numero di pannelli presenti; di volta in volta i numeri di righe e colonne aumentano di 10 ciascuno
        for(int i = 0; i < (r*c); i++) { // ciclo sul numero totale di pannelli presenti 
            panel.get(i).setBackground(backgroundColor); // imposto il colore di sfondo originale
        }
        rN += 10; // imposto un nuovo numero di righe, aumentando di 10 
        cN += 10 ; // imposto un nuovo numero di colonne, aumentando di 10
        for(int i = (r*c); i < (rN * cN) ; i++) // ciclando dal numero di pannelli presenti fino al numero di pannelli che dovranno esserci in base al nuovo numero di righe e al nuovo numero di colonne
            panel.add(new JPanel()); // aggiungo all'ArrayList un nuovo pannello JPanel
        setLayout(new GridLayout(rN,cN)); // imposto il nuovo layout con un nuovo numero di righe e un nuovo numero di colonne
        int j = 0; // dichiaro e inizializzo j
        for(j = (r*c); j < (rN*cN); j++){ // per ogni pannello aggiunto
            MyKeyboardAdapter adapter = new MyKeyboardAdapter(); // dichiaro e inizializzo l'adapter per la tastiera 
            panel.get(j).addKeyListener(adapter); // aggiungo un KeyListener che riceverà gli eventi da tastiera, che verranno gestiti dall'adapter
            panel.get(j).setFocusable(true); // faccio in modo che il pannello panel[n] sia "focusable", possa essere "focalizzato"
            add(panel.get(j)); // aggiungo il pannello al frame 
            panel.get(j).setBackground(backgroundColor); // imposto il colore di sfondo del pannello 
        }
        r = rN; // aggiorno il numero di righe 
        c = cN; // aggiorno il numero di colonne
        getContentPane().invalidate(); // "invalido" il content pane 
        getContentPane().validate(); // "valido" il content pane
        getContentPane().repaint(); // ridipingo il content pane
    }

    class synthSounds { // classe dedicata alla creazione dei suoni
        
        private void play(int[] rgb, double[] hsb){ // metodo che permette la generazione di suoni, la sintesi sonora
            int somma = 0; // dichiaro una variabile che conterrà la somma dei valori RGB
            for(int i = 0; i < 3; i++) // clico sulla dimensione dell'array contenente i valori RGB
                somma += rgb[i]; // sommo i valori r, g e b della codifica RGB del colore di sfondo del pannello corrente
            Synthesizer synth = JSyn.createSynthesizer(); // inizializzo il sintetizzatore
            UnitOscillator u = rgbToTimbre(somma); // chiamo il metodo che prende in ingresso la somma dei valori RGB che influenza il timbro, restituendo uno specifico tipo di oscillatore
            LineOut lineOut = new LineOut();  // inizializzo la lineOut
            synth.add(u); // aggiungo l'oscillatore al sintetizzatore
            synth.add(lineOut); // aggiungo lineOut al sintetizzatore
            u.output.connect(0, lineOut.input, 0); // collego l'output dell'oscillatore (ha una sola parte output, quindi 0) alla parte 0 (canale sinistro) dell'input di lineOut
            u.output.connect(0, lineOut.input, 1); // collego l'output dell'oscillatore (ha una sola parte output, quindi 0) alla parte 1 (canale destro) dell'input di lineOut
            synth.start(); // avvio il sintetizzatore
            double timeNow = synth.getCurrentTime(); // prendo il tempo corrente del sintetizzatore
            TimeStamp timeStamp = new TimeStamp(timeNow); // creo un timeStamp, un "attimo temporale" 
            lineOut.start(timeStamp); // faccio n modo che lineOut, responsabile dell'output in uscita, parta all'istante definito precedentemente 
            double freq = HueToFreq(hsb[0]); // chiamo il metodo che converte il valore H della codifica HSB in un valore di frequenza in Hz
            double dur = hsb[1]; // range[0 - 1]; la durata della nota è dettata dal valore S della codifica HSB del colore generato per il pannello corrente
            if(dur < 0.2) // se la durata è minore di 0.2, per evitare suoni troppo brevi
                dur = 0.2; // la imposto ad un valore minimo
            double amp = hsb[2]; // range[0 - 1]; l'ampiezza della nota è dettata dal valore B della codifica HSB 
            u.frequency.set(freq); // imposto la frequenza dell'oscillatore alla frequenza restituita dalla funzione HueToFreq, dunque condizionata dall'Hue di HSB
            u.amplitude.set(amp); //imposto l'ampiezza dell'oscillatore all'ampiezza amp, dettata dal valore B della codifica HSB
            try { 
                synth.sleepUntil(timeStamp.getTime() + dur); // il sintetizzatore dorme fino al tempo indicato, dunque fino al tempo corrente in timStamp + 0.3 secondi
            } catch (InterruptedException e) { // gestisco l'eccezione
                e.printStackTrace(); // nel caso in cui si verifichi l'eccezione faccio in modo che mi venga stampato un codice di errore riportante anche il metodo che l'ha causato
            }
            synth.stop(); // fermo il sintetizzatore
        }
        
        public void playSample(){ // metodo che si occupa di riprodurre un campione audio 
            Synthesizer synth = JSyn.createSynthesizer(); // creo un sintetizzatore
            LineOut lineOut = new LineOut();  // inizializzo la lineOut
            VariableRateDataReader samplePlayer; // creo un samplePlayer in grado di leggere il file audio
            synth.start(); // avvio il sintetizzatore
            synth.add(lineOut = new LineOut()); // aggiungo lineOut al sintetizzatore
            File sampleFile = new File("C:\\Users\\Giulia\\Music\\Twin_Peaks_theme.wav"); // dichiaro e inizializzo il file audio, indicando il path assoluto
            // Attenzione: path del file da modificare! 
            FloatSample sample;  // struttura dati che immagazzina i dati del file audio (dati immagazzinati come float a 32 bit)
            try {
                sample = SampleLoader.loadFloatSample(sampleFile); // carico i dati del file audio nella struttura dati apposita a contenerli
                if (sample.getChannelsPerFrame() == 1) { // se il file audio è mono, ha un solo canale 
                    synth.add(samplePlayer = new VariableRateMonoReader()); // allora utilizzerò VariableRateMonoReader
                    samplePlayer.output.connect(0, lineOut.input, 0); // connetto la parte 0 di output del samplePLayer alla parte 0 di input di lineOut 
                } else if (sample.getChannelsPerFrame() == 2) { // se il file audio è stereo, ha due canali
                    synth.add(samplePlayer = new VariableRateStereoReader()); // allora utilizzo VariableRateStereoReader
                    samplePlayer.output.connect(0, lineOut.input, 0); // connetto la parte 0 di output del samplePlayer alla parte 0 di input del lineOut, in modo tale che i dati del canale sinistro del file audio siano connessi al canale sinistro di input di lineOut
                    samplePlayer.output.connect(1, lineOut.input, 1); // connetto la parte 1 di output del samplePlayer alla parte 1 di input del lineOut, in modo tale che i dati del canale destro del file audio siano connessi al canale destro di input di lineOut
                } else { // altrimenti, se il file audio non è mono e non è nemmeno stereo
                    throw new RuntimeException("Il formato del sample audio non è supportato."); // fornisco un'eccezione dovuta al fatto che non gestiscofile audio che abbiano più di due canali
                }
                samplePlayer.rate.set(sample.getFrameRate()); // imposto la frequenza di lettura dei frame al frame rate originale 
                lineOut.start(); // avvio lineOut 
                samplePlayer.dataQueue.queue(sample); //accodo i campioni passati
            } catch (IOException ex) {
                System.out.println("Errore! File inserito non esistente.");
            }
        }
    }
    
    public int[] pastelColors(){ // generatore random di colori pastello
        // stabilisco dei valori RGB randomici
        int red = (int) (Math.random() * 256); // valore di Red di RGB
        int green = (int) (Math.random() * 256); // valore di Green di RGB
        int blue = (int) (Math.random() * 256); // valore di Blue di RGB
        // per generare colori pastello "aggiungo" del bianco, dunque sommo 255 e divido il risultato per 2 in modo tale che i valori risultanti abbiano dei valori RGB piuttosto alti (più alti della media), tipico dei colori pastello
        red = (red + 255) /2;
        green = (green + 255) /2;
        blue = (blue + 255) /2;
        
        //Color color = new Color(red, green, blue); // creo un nuovo colore con tali valori RGB
        //return color; // restituisco il colore
        int[] valuesRGB = {red, green, blue}; // dichiaro un array che conterrà i valori RGB appena determinati
        return valuesRGB; // restituisco l'array contenente i valori RGB
    }
    
    public double[] rgbToHsb(int[] rgb){ // metodo che prende in ingresso un array contenente valori RGB, li converte in valori HSB e restituisce un array contenente i valori convertiti in HSB
        double[] valuesHSB = new double[3]; // vettore che conterrà i valori HSB (HSV)
        double r = rgb[0] / 255.00; // normalizzo il valore r della codifica RGB in modo tale che abbia valore compreso tra 0 e 1
        double g = rgb[1] / 255.00; // normalizzo il valore g della codifica RGB in modo tale che abbia valore compreso tra 0 e 1
        double b = rgb[2] / 255.00; // normalizzo il valore b della codifica RGB in modo tale che abbia valore compreso tra 0 e 1
        double max, min, delta; // dichiaro le variabili che mi serviranno per memorizzare massimo, minimo, delta tra massimo e minimo
        
        max = r > g ? (r > b ? r : b) : (g > b ? g : b ); // recupero il massimo tra r, g e b
        min = r < g ? (r < b ? r : b) : ( g < b ? g : b); // recupero il minimo tra r, g e b
        delta = max - min; // faccio la differenza tra il minimo e il massimo
        
        // calcolo i valori HSB a partire dai valori RGB
        // calcolo del valore relativo alla tonalità (Hue) - H
        // il calcolo si differenzia in base a quale dei valori r, g e b sia il massimo
        if( max == r){ // nel caso r sia maggiore di g e b 
            if ( g > b ) // nel caso in cui g sia maggiore di b 
                valuesHSB[0] = (((g - b) / delta )) * 60; // formula per ricavare il valore H 
            else // altrimenti, nel caso in cui la differenza tra g e b sia negativa calcolo ((g - b)/delta) modulo 6 
                valuesHSB[0] = (((g - b) / delta ) + 6 ) * 60; 
        } else if( max == g){ // nel caso g sia maggiore di b e r 
            valuesHSB[0] = (((b - r) / delta ) + 2 ) * 60;
        } else{ // nel caso b sia maggiore di r e g 
            valuesHSB[0] = (((r - g) / delta ) + 4 ) * 60;
        } 
        // calcolo il valore relativo alla saturazione (Saturation) - S
        if( max  < Double.MIN_VALUE) // se il massimo è nullo o comunque minore del più piccolo valore double consentito
            valuesHSB[1] = 0; // imposto il valore di S uguale a 0
        else // se il massimo non è nullo
            valuesHSB[1] = delta / max; // il valore di S è uguale alla differenza tra massimo e minimo fratto il valore del massimo
        // calcolo il valore relativo alla luminosità (Brightness) - B
        valuesHSB[2] = max; // valore di B che corrisponde al massimo tra i valori RGB normalizzati
        
        return valuesHSB; // restituisco l'array contenente i valori HSB
    }
    
    public double HueToFreq(double h){ // metodo che prende in ingresso il valore H della codifica HSB, che condiziona la frequenza restituita
        int j = 50; // passo 
        double f; // dichiaro la variabile f che conterrà la frequenza
        if (h <= j ) // se il valore H di HSB è troppo piccolo, minore o uguale di 50
            f = 200; // altrimenti non si sentirebbe (per range 20-20000 Hz)
        else if(h > j && h <= j*5) // valore di H compreso tra 51 e 250 compreso
            f = h * 4;
        else // valore di H maggiore di 240 (251-360)
            f = 1500;
        return f; // restituisco la frequenza
    }
    
    public UnitOscillator rgbToTimbre(int s){ // metodo che in base ai valori RGB (nello specifico in base alla somma di questi tre valori) determina la scelta di un timbro
        // RGB condiziona il timbro 
        UnitOscillator u; // dichiaro uno UnitOscillatore generale
            if(s >=  550){ // se la somma dei valori RGB è maggiore uguale di 550 
                int n = (int) (Math.random() * 3 ); // prendo un numero casuale da 0 a 2
                if(n == 0) // se 0
                    u = new SineOscillator(); // sarà un oscillatore sinusoidale
                else if(n == 1) // se 1 
                    u = new TriangleOscillator(); // l'oscillatore avrà onda triangolare
                else // altrimenti, se 2
                    u = new SquareOscillator(); // l'oscillatore avrà un'onda quadrata
            } else { // altrimenti, se la somma è minore di 550
                int n = (int) (Math.random() * 3 ); // prendo un numero casuale da 0 a 2
                if(n == 0) // se 0
                    u = new RedNoise(); // avrò del rumore rosso
                else if(n == 1) // se 1
                    u = new SawtoothOscillator(); // l'oscillatore avrà un'onda a dente di sega
                else // altrimenti, se 2
                    u = new SquareOscillator(); // l'oscillatore avrà un'onda quadrata
            }
        return u; // restituisco l'oscillatore
    }  
}
