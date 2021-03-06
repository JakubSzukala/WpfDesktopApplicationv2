using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Text;
using WpfDesktopApplicationv2.Commands;
using WpfDesktopApplicationv2.Converters;
using WpfDesktopApplicationv2.Stores;
using System.Windows.Media;
using WpfDesktopApplicationv2.Models;

namespace WpfDesktopApplicationv2.ViewModels
{
    public class LedControlViewModel : INotifyPropertyChanged
    {
        enum RGB { R, G, B}

        // properties
        private List<LedCommand> _leds;
        public List<LedCommand> Leds
        {
            get => _leds;
            set
            {
                _leds = value;
                OnPropertyChanged("Leds");
            }
        }

        public ObservableCollection<Color> LedColorSource { get; set; }

        private ObservableCollection<ObservableCollection<int[]>> _stateMatrix; // this does not have to be Observable collection
        public ObservableCollection<ObservableCollection<int[]>> StateMatrix
        {
            get => _stateMatrix;
            set
            {
                _stateMatrix = value;
                UpdateColorSource();
            }
        }

        private string _ipAddressBox;

        public string IpAddressBox
        {
            get { return _ipAddressBox; }
            set
            {
                _ipAddressBox = value;
                OnPropertyChanged("IpAddressBox");
            }
        }

        private string _upToDate;

        public string UpToDate
        {
            get { return _upToDate; }
            set 
            { 
                _upToDate = value;
                OnPropertyChanged(nameof(UpToDate));
            }
        }


        // fields
        private readonly int dimX, dimY;
        private readonly BroadcastLedSelectedStore _broadcastCoordinates;
        private readonly ServerMediatorModel _mediator;
        private ConfigModel _config;
        private ErrorStore _errorStore;

        // sliders (not necesarily props?)
        public SliderViewModel SliderR { get; set; }
        public SliderViewModel SliderG { get; set; }
        public SliderViewModel SliderB { get; set; }

        // sliders store classes
        private readonly SliderValueChangedStore sliderRValueChanged;
        private readonly SliderValueChangedStore sliderGValueChanged;
        private readonly SliderValueChangedStore sliderBValueChanged;

        // canvas preview
        private CanvasPreviewViewModel _canvas;

        // buttons 
        public ButtonCommand UpdateButton { get; set; }

        public CanvasPreviewViewModel Preview
        {
            get => _canvas;
            set
            {
                _canvas = value;
                OnPropertyChanged(nameof(Preview));
            }
        }

        public LedControlViewModel()
        {
            dimX = 8;
            dimY = 8;

            _errorStore = new ErrorStore();
            _errorStore.ErrorState = " ";

            // initialization of default version of state matrix and color source
            LedColorSource = new ObservableCollection<Color>();
            GenerateStateMatrix();
            UpdateColorSource();

            // initialize mediator object
            _broadcastCoordinates = new BroadcastLedSelectedStore();

            // subscribe to an event and listen for broadcast of selected LED coordinates
            _broadcastCoordinates.LedSelected += ReceiveLedSelected;

            // init led commands 
            InitLedCommandsList();

            // init sliders with their store class
            sliderRValueChanged = new SliderValueChangedStore();
            sliderGValueChanged = new SliderValueChangedStore();
            sliderBValueChanged = new SliderValueChangedStore();

            SliderR = new SliderViewModel(sliderRValueChanged);
            SliderG = new SliderViewModel(sliderGValueChanged);
            SliderB = new SliderViewModel(sliderBValueChanged);

            // init canvas preview
            Preview = new CanvasPreviewViewModel(sliderRValueChanged, sliderGValueChanged, sliderBValueChanged);

            // config and server mediator setup 
            _config = new ConfigModel(1F, "192.168.56.5", 10);
            _mediator = new ServerMediatorModel(_config.IpAddress, _errorStore);

            // initialize buttons
            UpdateButton = new ButtonCommand(PostLedControlWrapper);

            UpToDate = "Yes";
        }

        private void ReceiveLedSelected(object sender, KeyValuePair<int, int> coordinates)
        {
            StateMatrix[coordinates.Key][coordinates.Value][(int)RGB.R] = (int)Preview.CanvasColor.R;
            StateMatrix[coordinates.Key][coordinates.Value][(int)RGB.G] = (int)Preview.CanvasColor.G;
            StateMatrix[coordinates.Key][coordinates.Value][(int)RGB.B] = (int)Preview.CanvasColor.B;
            UpdateColorSource();
        }

        private void InitLedCommandsList()
        {
            _leds = new List<LedCommand>();
            for (int i = 0; i < dimX; i++)
            {
                for (int j = 0; j < dimY; j++)
                {
                    _leds.Add(new LedCommand(i, j, _broadcastCoordinates));
                }
            }
        }

        private void GenerateStateMatrix()
        {
            _stateMatrix = new ObservableCollection<ObservableCollection<int[]>>();
            for(int i = 0; i < dimX; i++)
            {
                _stateMatrix.Add(new ObservableCollection<int[]>());
                for (int j = 0; j < dimY; j++)
                {
                    _stateMatrix[i].Add(new int[] { 0, 0, 0 });
                }
            }
        }

        private void UpdateColorSource()
        {
            if(LedColorSource.Count == 0)
            {
                for (int row = 0; row < dimX; row++)
                {
                    for (int column = 0; column < dimY; column++)
                    {
                        LedColorSource.Add(Color.FromArgb(
                            150,
                            (byte)StateMatrix[row][column][(int)RGB.R],
                            (byte)StateMatrix[row][column][(int)RGB.G],
                            (byte)StateMatrix[row][column][(int)RGB.B]
                            ));
                    }
                }
            }
            else
            {
                for (int row = 0; row < dimX; row++)
                {
                    for (int column = 0; column < dimY; column++)
                    {
                        LedColorSource[(row * dimX) + column] = Color.FromArgb(
                            150,
                            (byte)StateMatrix[row][column][(int)RGB.R],
                            (byte)StateMatrix[row][column][(int)RGB.G],
                            (byte)StateMatrix[row][column][(int)RGB.B]
                            );
                    }
                }
            }

            UpToDate = "No";
        }

        private void PostLedControlWrapper()
        {
            _mediator.PostLedControl(StateMatrix);
            UpToDate = "Yes";
        }

        public event PropertyChangedEventHandler PropertyChanged;
        private void OnPropertyChanged(string propName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propName));
        }
    }
}
