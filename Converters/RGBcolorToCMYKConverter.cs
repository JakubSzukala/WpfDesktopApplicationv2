using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Windows.Data;
using System.Windows.Media;

namespace WpfDesktopApplicationv2.Converters
{
    class RGBcolorToCMYKConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            Color c = (Color)value;
            int R = c.R;
            int G = c.G;
            int B = c.B;

            float Rprime = (float)R / 255;
            float Gprime = (float)G / 255;
            float Bprime = (float)B / 255;

            float K = 1 - Math.Max(Rprime, Math.Max(Gprime, Bprime));
            float C = (1 - Rprime - K) / (1 - K);
            float M = (1 - Gprime - K) / (1 - K);
            float Y = (1 - Bprime - K) / (1 - K);

            return new string(
                "C: " + Math.Round(C,2) 
                + " M: " + Math.Round(M, 2)
                + " Y: " + Math.Round(Y, 2)
                + " K: " + Math.Round(K, 2)
                );
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            return null;
        }
    }
}
