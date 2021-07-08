using System;
using System.Collections.Generic;
using System.Text;
using WpfDesktopApplicationv2.Stores;
using System.Windows.Media;
using System.ComponentModel;

namespace WpfDesktopApplicationv2.ViewModels
{
    public class CanvasPreviewViewModel : INotifyPropertyChanged
    {
        private Color _canvasColor;
        public Color CanvasColor
        {
            get => _canvasColor;
            set
            {
                _canvasColor = value;
                OnPropertyChanged(nameof(CanvasColor));
            }
        }

        private SliderValueChangedStore storeR;
        private SliderValueChangedStore storeG;
        private SliderValueChangedStore storeB;

        

        public CanvasPreviewViewModel(SliderValueChangedStore srcR, SliderValueChangedStore srcG, SliderValueChangedStore srcB)
        {
            storeR = srcR;
            storeG = srcG;
            storeB = srcB;

            storeR.SliderValueChanged += UpdateCanvasColor;
            storeG.SliderValueChanged += UpdateCanvasColor;
            storeB.SliderValueChanged += UpdateCanvasColor;
        }

        private void UpdateCanvasColor()
        {
            Color temp = new Color();
            temp = Color.FromArgb(
                150,
                (byte)storeR.CurrentSliderValue,
                (byte)storeG.CurrentSliderValue,
                (byte)storeB.CurrentSliderValue);
            CanvasColor = temp;
        }

        public event PropertyChangedEventHandler PropertyChanged;
        private void OnPropertyChanged(string propName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propName));
        }
    }
}
