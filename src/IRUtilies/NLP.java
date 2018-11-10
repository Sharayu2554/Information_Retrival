package src.IRUtilies;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;
import java.util.Properties;

public class NLP {
    private Properties props = new Properties();
    private StanfordCoreNLP pipeline;
    private Porter porter = new Porter();
    private Annotation annotation;

    public NLP() {
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public String getStemma(String input) {
        return porter.stripAffixes(input);
    }

    public Annotation annotateData(String data) {
        annotation = new Annotation(data);
        pipeline.annotate(annotation);
        return annotation;
    }

    public List<CoreLabel> getCoreLabels() {
        return this.annotation.get(CoreAnnotations.TokensAnnotation.class);
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public StanfordCoreNLP getPipeline() {
        return pipeline;
    }

    public void setPipeline(StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }

    public Porter getPorter() {
        return porter;
    }

    public void setPorter(Porter porter) {
        this.porter = porter;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
}
