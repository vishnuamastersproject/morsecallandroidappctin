package cha.com.autodetectsms;


public class ValueDTO {
   public String alpha,dottie;

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getDottie() {
        return dottie;
    }

    @Override
    public String toString() {
        return "ValueDTO{" +
                "alpha='" + alpha + '\'' +
                ", dottie='" + dottie + '\'' +
                '}';
    }

    public void setDottie(String dottie) {
        this.dottie = dottie;
    }
}
