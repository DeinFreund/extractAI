package zkai;

import java.awt.*;
import java.awt.image.*;
import java.util.Objects;

public class AdditiveComposite implements Composite {

    public AdditiveComposite() {
    }

    public CompositeContext createContext(ColorModel srcColorModel,
            ColorModel dstColorModel, RenderingHints hints) {
        return new AdditiveCompositeContext();
    }
}