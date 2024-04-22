# Apache storm

## Ejercicio: Contar palabras de un flujo interminable de frases

Se han manejado una cantidad importante de conceptos que pueden ser confusos, por lo que aplicaremos los conceptos anteriormente descritos en un ejemplo. En este repositorio encontraremos un ejemplo que pueden ejecutar.

Para ejecutar este ejemplo solo necesitamos Java 1.8 o superior.

Luego de haber clonado el repositorio podemos ejecutar la topología del ejemplo con el comando "./gradlew run".

Antes de ejecutar entendamos un poco que va a realizar el ejercicio. En el ejercico tenemos un Spout que genera frases simulando un flujo interminable de frases y luego una serie de Bolts que procesan dichas frases para contar las palabras de interés que se van produciendo en el flujo.

Para realizar esto se crea un Spout que es el encargado de emitir las frases (SendPhrasesSpout), un Bolt que se encarga de tomar un frase y partirla es sus palabras (PhraseToWordsBolt), un Bolt que se encarga de normalizar las palabras pasándolas todas a minúsculas (ToLowerCaseBolt), otro Bolt que quita las palabras que no nos interesan contar (FilterWordsBolt) y un Bolt final que cuenta cada palabra (WordCountBolt). Esta topología queda definida en la clase main StormExampleMain como se muestra a continuación.

```java
final TopologyBuilder topologyBuilder = new TopologyBuilder();
topologyBuilder.setSpout("SendPhrasesSpout", new SendPhrasesSpout(), 10);
topologyBuilder.setBolt("PhraseToWordsBolt", new PhraseToWordsBolt(), 15).shuffleGrouping("SendPhrasesSpout");
topologyBuilder.setBolt("ToLowerCaseBolt", new ToLowerCaseBolt(), 40).shuffleGrouping("PhraseToWordsBolt");
topologyBuilder.setBolt("FilterWordsBolt", new FilterWordsBolt(), 20).shuffleGrouping("ToLowerCaseBolt");
topologyBuilder.setBolt("WordCountBolt", new WordCountBolt(), 10).
        fieldsGrouping("FilterWordsBolt", new Fields("word"));
```

En este ejemplo se puede ver la definición de los Spouts y Bolts y el nivel de paralelismo definido en cada uno (cantidad de Tasks que lo ejecutan).

Como se ve en el código del ejercicio cada Bolt se suscribe a un Spout o un Bolt para recibir las tuplas que estos emiten. En el código se ve como todos se conectan a través de shuffleGrouping, salvo el ultimo que se conecta a través de fieldGrouping donde además se especifica un campo word, esto refiere al agrupamiento de Streams. En los primeros casos no es importante para el procesamiento a que Task del Bolt va la tupla, pero al tener que contar las palabras necesitamos que una misma palabra valla a una misma task, para no tener un count en una task y otro count en otra task.

Con el codigo anterior queda totalmente definida la topología, pero veamos ahora como es la definición de un Spout.

```java
public class SendPhrasesSpout extends BaseRichSpout {

    private static final String[] phrases = {"Hola como estas", "Hola como te va", "Este es otro ejemplo",
            "Ejemplo es lo que sobra", "Estas en el horno", "El horno de mi mama", "Estas fraces son un ejemplo de encaje",
            "Encaje tiene el vestido de mi mama", "Mama es mal", "Mama es buena", "Vamos los pibes", "Pibes era un perfume",
            "El perfume un gran libro es", "Ahora le pinto Yoda"};

    private SpoutOutputCollector collector;

    @Override
    public void open(final Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        int rnd = new Random().nextInt(phrases.length);
        collector.emit(Collections.singletonList(phrases[rnd]));
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("phrase"));
    }

}
```

En este caso implementamos el formato típico de Spouts a través del BaseRichSpout. En este caso solo tenemos que implementar tres métodos, el open para la definición del Spout, el nextTuple que se ejecuta cada tanto tiempo para emitir un valor, y el declareOutputFields, el cual define el formato de las tuplas de salida. Solo con estas definiciones ya tenemos un Spout.

La definición de un Bolt no es muy diferente como queda mostrado en el siguiente ejemplo.

```java
public class PhraseToWordsBolt extends BaseRichBolt {

    private OutputCollector collector;

    @Override
    public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(final Tuple input) {
        final String phrase = input.getStringByField("phrase");
        Arrays.stream(phrase.split(" ")).forEach(w -> collector.emit(Collections.singletonList(w)));
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

}
```

La única diferencia importante entre un Bolt y un Spout es que el primero define un execute en vez de un nextTuple, y éste recibe una tupla en vez de crearla, como se ven en el ejemplo.

Tanto en el Spout como en el Bolt para emitir una tupla se ejecuta la función emit.

Pueden ver el resto de las definiciones de la topologia para entender mejor cómo funciona. Al ejecutar el ejemplo tal cual está, deben de tener como resultado 10 archivos con el numero de Task y las palabras contadas por cada una, y una palabra no debería aparecer en distintas Tasks. El numero 10 es debido a que el ultimo Bolt tiene 10 en el paralelismo, al aumentar o disminuir este numero cambiara la cantidad de archivos en ese valor.

Al ejecutar el ejemplo, se debe tener en cuenta que una topología no termina, por lo que va a quedar ejecutando interminablementa hasta que la detengamos.

### Modificacion ejercicio:
Ahora que ya comprendemos como funciona Apache Storm, y tenemos claro como se puede ejecutar a traves de Java, vamos a modificar el ejercicio anterior para que cambiemosel generador de frases.

En la clase SendPhrasesSpout.java cambiamos el arreglo de string phrases por el siguiente:

```java
private static final String[] phrases = {
            "Puedo escribir los versos más tristes esta noche,",
            "Escribir por ejemplo La noche está estrellada,",
            "y tiritan azules los astros a lo lejos",
            "Puedo escribir los versos más tristes esta noche",
            "Yo la quise y a veces ella también me quiso",
            "En las noches como ésta la tuve entre mis brazos",
            "La besé tantas veces bajo el cielo infinito",
            "Ella me quiso a veces yo también la quería",
            "Cómo no haber amado sus grandes ojos fijos",
            "Puedo escribir los versos más tristes esta noche",
            "Piensa en mí que soy así, como te decía",
            "La noche está estrellada y ella no está conmigo",
            "Este es el último dolor que ella me causa",
            "Y estos son los últimos versos que le escribo"
    };
```

Luego de este cambio volvemos ejecutar el archivo y las palabras y cantidad que se generaron cambiaran, adjunte el contador que se genero en cada archivo en el informe del ejercicio.

### Modificacion libre

Con el codigo existente y habiendo desarrollado la modificacion anterior y viendo como el resultado cambia. Desarrolle ajustes al codigo, que modifiquen de alguna manera la salida que se tiene actualmente, un ejemplo de ello puede ser cambiar el conteo de palabras a frases con un conjunto de palabras pequeñas, esto para ver como se repiten dichas frases generadas en el spout.

## Entregable
Un documento donde se evidencia los resultados de las tres partes del taller, ademas de una breve explicacion de que genera el cambio que realizo en el ultimo punto.

Ademas de dar una breve descripcion de como funcionan los bolts y los spout, para utilizar apache storm.

## Referencias

1 * [Apache Storm](http://storm.apache.org/)


