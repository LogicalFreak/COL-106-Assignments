#include<bits/stdc++.h>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
using namespace std ;
using namespace cv ;
char si = 'S';
char di = 'D';
Mat source_image;
Mat destination_image;
long NumOutputImages;
long NumClicks;
long sclicks=0 ;
long dclicks=0 ;
struct Triple{
        long i1 ;
        long i2 ;
        long i3 ;
        void make_triple(long a,long b,long c){
           i1=a ;
           i2=b ;
           i3=c ;
        }

    } ;
class Color{
	private:
		long red ;
		long green ;
		long blue ;

	public:
		Color(){
			setColor(0, 0, 0);
		}
		void setColor(long r, long g, long b){
			setRed(r);
			setGreen(g);
			setBlue(b);
		}
		void setRed(long r){
			red = r;
		}
		void setGreen(long g){
			green = g;
		}
		void setBlue(long b)	{
			blue = b;
		}
		long getRed() const{
			return red;
		}
		long getGreen() const{
			return green;
		}
		long getBlue() const{
			return blue;
		}
} ;
class Image
{
    public:
		char type[5];
		long width, height;
		long max_val;
	    Color **color ;
	    vector< pair<long,long> > CoOrds ;
		Image(){
			strcpy(type, "\0");
			width = 0;
			height = 0;
			max_val = 0;
			color = NULL;
		}
		void fillvalues(char t[5] , long w , long h , long mv){
			for(long i=0 ; i<5 ; i++){
				type[i]=t[i] ;
			}
			max_val=mv ;
			width=w ;
			height=h ;
		}
        void takecord(long x,long y){
			CoOrds.push_back(make_pair(x,y)) ;
		}
        Mat convertToCvImage(){
			Mat img(height, width, CV_8UC3);
			for(long i = 0; i < height; i++){
				for(long j = 0; j < width; j++){
					img.at<Vec3b>(i, j) = Vec3b(color[i][j].getBlue(), color[i][j].getGreen(), color[i][j].getRed());
				}
			}
			return img;
		}
		static bool writeVideo(Image images[], long num_images, char filename[]){
					if(num_images == 0){
						cerr << "No frames to write" << endl;
						return false;
					}
					VideoWriter outputVideo;
					Size frame_size = Size(images[0].width, images[0].height);
					outputVideo.open(filename, 861292868, 25, frame_size, true);	// 3rd argument is the frame rate
					if (!outputVideo.isOpened()){
						cerr << "Unable to write video: " << filename << endl;
						return false;
					}
					for(long f = 0; f < num_images; f++){
						outputVideo << images[f].convertToCvImage();
					}
					return true;
		}
		void PushDefaultCordValues(){
			CoOrds.push_back(make_pair(0,0)) ;
			CoOrds.push_back(make_pair(width-1,0)) ;
			CoOrds.push_back(make_pair(0,height-1)) ;
			CoOrds.push_back(make_pair(width-1,height-1)) ;

		}
		bool readImage(char filename[]){
			ifstream file;
			file.open(filename , ios::in);
			if(file.good()){
				file >> type;
				file >> width >> height;
				file >> max_val;
				PushDefaultCordValues() ;
				color = new Color*[height];
				for(long i = 0; i < height; i++){
					color[i] = new Color[width];
					for(long j = 0; j < width; j++){
				 		long r,g,b;
						file >> r >> g >> b;
						color[i][j].setColor(r, g, b);
					}
				}
				file.close();
				return true;
			}
			else{
				cerr << "Error reading image: " << filename << endl;
				return false;
			}
		}
		bool writeImage(char filename[]){
			ofstream file;
			file.open(filename, ios::out);
			if(file.good()){
				file << type << endl;
				file << width << " " << height << endl;
				file << max_val << endl;
				for(long i=0 ; i<height ; i++){
					for(long j=0 ; j<width ; j++){
						file << color[i][j].getRed() << " " << color[i][j].getGreen() << " " << color[i][j].getBlue() << endl;
					}
				}
				file.close();
				return true;
			}
			else{
				cerr << "Error writing file: " << filename << endl ;
				return false;
			}
		}

};
Image source = Image() ;
Image det = Image() ;
Image iges[100] ;
class Triangulation{

    public : long n ;
    private : vector<long> eP ;
    public : vector<long> sP ;
    public  : vector<Triple> Triangle ;
    private : vector< pair<long,long> > coOrds  ;
    private : vector< pair<long,long> > scoOrds ;
    private : static bool comp(const pair<long,long> &p1 ,const  pair<long,long> &p2){
        if(p1.first < p2.first)
           return true ;
        else
           if(p1.first == p2.first)
                return p1.second < p2.second ;
           else
                return false ;
    }
    private : static bool comptriple(const Triple &t1 , const Triple &t2){
        if(t1.i1<t2.i1)
            return true ;
        else
            if(t1.i1==t2.i1){
                 if(t1.i2<t2.i2)
                     return true ;
                 else{
                     if(t1.i2==t2.i2)
                         return t1.i3<t2.i3 ;
                     else
                         return false ;
                 }
             }
             else
                return false ;
   }
    private : static bool equals(const pair<long,long> &p1 , const pair <long,long> &p2){
            return  (p1.first==p2.first && p1.second==p2.second) ;
   }
    private : long search(long i){
        pair<long,long> p = coOrds[i] ;
        long low=1 ;
        long high=n ;
        long mid ;
        while(high>low){
            mid = (low+high)/2 ;
            if(equals(p,scoOrds[mid]))
                return mid ;
            if(comp(p,scoOrds[mid]))
                 high=mid-1 ;
            else
                 low=mid+1 ;
        }
        return low ;
    }
    private : bool check(long a,long b,long c,long d){
         long a1 = coOrds[a].first ;  long a2 = coOrds[a].second ;
         long b1 = coOrds[b].first ;  long b2 = coOrds[b].second ;
         long c1 = coOrds[c].first ;  long c2 = coOrds[c].second ;
         long d1 = coOrds[d].first ;  long d2 = coOrds[d].second ;
         long l1 = (d2-c2)*(a1-c1)-(d1-c1)*(a2-c2) ;
         long l2 = (d2-c2)*(b1-c1)-(d1-c1)*(b2-c2) ;
         bool t1  = (l1*l2)<=0 ;
         l1 = (b2-a2)*(c1-a1)-(b1-a1)*(c2-a2) ;
         l2 = (b2-a2)*(d1-a1)-(b1-a1)*(d2-a2) ;
         bool t2 = (l1*l2)<=0 ;
         return t1 && t2 ;
    }
    private : void addTriangle(long a,long b,long c){
         long ar[3] ;
         ar[0]=a ; ar[1]=b ; ar[2]=c ; sort(ar,ar+3) ;
         Triple T ;
         T.make_triple(ar[0],ar[1],ar[2]) ;
         Triangle.push_back(T) ;
    }
    private : void addTriangles(long i){
         if(i==1){
            eP.push_back(sP[1]) ;
            return ;
         }
         if(i==2){
             eP.push_back(sP[2]) ;
             return  ;
         }
         long size = eP.size() ;
         long k,m,l ;
         vector<long> validpoints ;
         for(long j=0 ; j<size ; j++){
              k=sP[i] ;
              m=eP[j] ;
              for(l=1 ; l<size-1 ; l++){
                  if(check(k,m,eP[(j+l)%size],eP[(j+l+1)%size]))
                       break ;
              }
              if(l==size-1)
                  validpoints.push_back(j) ;
         }
         long ptsize = validpoints.size() ;
         for(long j=0 ; j<ptsize-1 ; j++)
              addTriangle(k,eP[validpoints[j]],eP[validpoints[j+1]]) ;
         long fir = validpoints[0] ;
         long las = validpoints[ptsize-1] ;
         if(fir+2<las)
               eP.erase(eP.begin()+fir+1,eP.begin()+las) ;
         else
               if(fir+2==las)
                    eP.erase(eP.begin()+fir+1) ;
         eP.insert(eP.begin()+fir+1,k) ;
    }
    public  : Triangulation(long no_of_points , const vector< pair<long,long> > &co_ordinates ){
         n = no_of_points ;
         long size = co_ordinates.size() ;
         coOrds.push_back(make_pair(0,0)) ;
         for(long i=0 ; i<size ; i++){
              coOrds.push_back(co_ordinates[i]) ;
         }
         scoOrds=coOrds ;

    }
    public : Triangulation(){
         n=0 ;
    }
    public  : void Initialize(long no_of_points , const vector< pair<long,long> > &co_ordinates ){
             n = no_of_points ;
             long size = co_ordinates.size() ;
             coOrds.push_back(make_pair(0,0)) ;
             for(long i=0 ; i<size ; i++){
                  coOrds.push_back(co_ordinates[i]) ;
             }
             scoOrds=coOrds ;

        }

    public  : void PerformTriangulation(){
         for(long i=0 ; i<=n ; i++){
            sP.push_back(0) ;
         }
         sort(scoOrds.begin()+1,scoOrds.begin()+n+1,comp) ;
         for(long i=1 ; i<=n ; i++)
             sP[search(i)]=i ;
         for(long i=1 ; i<=n ; i++)
             addTriangles(i) ;
         sort(Triangle.begin(),Triangle.end(),comptriple) ;
    }
} ;
Triangulation machine ;
void on_mouse(int event, int x, int y, int flags, void *param){
	if(event == EVENT_LBUTTONDOWN){
		char im_type = *(char *) param;
		if(im_type == si){
			for(long i=y-2 ; i<=y+2 ; i++){
				for(long j=x-2 ; j<=x+2 ; j++){
					source_image.at<Vec3b>(i,j) = Vec3b(255,0,0) ;
				}
			}
			source.takecord(x,y) ;
			imshow("Source Image", source_image);
            waitKey(0) ;

		}
		else
			if(im_type == di){
			    for(long i=y-2 ; i<=y+2 ; i++){
				    for(long j=x-2 ; j<=x+2 ; j++){
					    destination_image.at<Vec3b>(i,j) = Vec3b(255,0,0) ;
				    }
			    }
			    det.takecord(x,y) ;
		        imshow("Destination Image", destination_image);
			        waitKey(0) ;
			    		}

		// TODO: Write your code for mouse click here
	}
}
long Area(const pair<long,long> p1 , const pair<long,long> p2 , const pair<long,long> p3){
    long x1,x2,x3,y1,y2,y3 ;
    x1 = p1.first ; x2=p2.first ; x3=p3.first ;
    y1 = p1.second ; y2=p2.second ; y3=p3.second ;
    long area=x1*(y2-y3)+x2*(y3-y1)+x3*(y1-y2) ;
    if(area>0)
       return area ;
    else
       return -area ;
}
bool checkinside(const long i , const Triple T ,const long x ,const long y){
     pair<long,long> p = make_pair(x,y) ;
     pair<long,long> p1 = iges[i].CoOrds[T.i1-1] ;
     pair<long,long> p2 = iges[i].CoOrds[T.i2-1] ;
     pair<long,long> p3 = iges[i].CoOrds[T.i3-1] ;
     long A = Area(p1,p2,p3) ;
     long a1 = Area(p,p1,p2) ;
     long a2 = Area(p,p2,p3) ;
     long a3 = Area(p,p3,p1) ;
     return A==a1+a2+a3 ;
}
Color FillPixel(const long i ,const long x ,const long y){
	vector<Triple> T = machine.Triangle ;
	long size = T.size() ;
	long j=0 ;
	for(j=0 ; j<size ; j++){
		if(checkinside(i,T[j],x,y))
			break ;
	}

	Triple t = T[j] ;
	long x1,x2,y1,y2,x3,y3,a1,a2,b1,b2,c1,c2 ;
	x1 = iges[i].CoOrds[t.i1-1].first ;
	x2 = iges[i].CoOrds[t.i2-1].first ;
	x3 = iges[i].CoOrds[t.i3-1].first ;
	y1 = iges[i].CoOrds[t.i1-1].second ;
	y2 = iges[i].CoOrds[t.i2-1].second ;
	y3 = iges[i].CoOrds[t.i3-1].second ;
	a1 = x2-x1 ; a2 = y2-y1 ;
	b1 = x3-x1 ; b2 = y3-y1 ;
	c1 = x-x1 ; c2 = y-y1 ;
	double b = (b2*c1-b1*c2)/((a1*b2-a2*b1)+0.0) ;
	double g = (a2*c1-a1*c2)/((a2*b1-a1*b2)+0.0) ;
	long sx,sy ;
    x1 = source.CoOrds[t.i1-1].first ;
    x2 = source.CoOrds[t.i2-1].first ;
    x3 = source.CoOrds[t.i3-1].first ;
    y1 = source.CoOrds[t.i1-1].second ;
    y2 = source.CoOrds[t.i2-1].second ;
    y3 = source.CoOrds[t.i3-1].second ;
    sx = (long) (b*(x2-x1)+g*(x3-x1)+x1) ;
    sy = (long) (b*(y2-y1)+g*(y3-y1)+y1) ;
    Color s = source.color[sy][sx] ;
    long dx,dy ;
    x1 = det.CoOrds[t.i1-1].first ;
    x2 = det.CoOrds[t.i2-1].first ;
    x3 = det.CoOrds[t.i3-1].first ;
    y1 = det.CoOrds[t.i1-1].second ;
    y2 = det.CoOrds[t.i2-1].second ;
    y3 = det.CoOrds[t.i3-1].second ;
    dx = (long) (b*(x2-x1)+g*(x3-x1)+x1) ;
    dy = (long) (b*(y2-y1)+g*(y3-y1)+y1) ;
    Color d = det.color[dy][dx] ;
    double a = i/(1.0+NumOutputImages) ;
    Color c ;
    c.setBlue((long) (s.getBlue()*(1-a)+d.getBlue()*a)) ;
    c.setRed((long) (s.getRed()*(1-a)+d.getRed()*a)) ;
    c.setGreen((long) (s.getGreen()*(1-a)+d.getGreen()*a)) ;
    return c ;


}
void CreateIntermediateImage(const long i){
	iges[i].fillvalues(source.type,source.width,source.height,source.max_val) ;
	long size = source.CoOrds.size() ;
	double a = i/(1.0+NumOutputImages)  ;
	for(long j=0 ; j<size ; j++){
		pair<long,long> ps = source.CoOrds[j] ;
		pair<long,long> pd = det.CoOrds[j] ;
		long x = (long) (ps.first*(1-a) + a*pd.first) ;
		long y = (long) (ps.second*(1-a) + a*pd.second) ;
		iges[i].takecord(x,y) ;
 	}
	long height = iges[i].height ;
	long width = iges[i].width ;
	iges[i].color = new Color*[height];
	for(long j=0 ; j<height ; j++){
		iges[i].color[j] = new Color[width];
		for(long k=0 ; k<width ; k++){
			iges[i].color[j][k] = FillPixel(i,k,j) ;
	    }
	}
}


int main(){

	    char src[50];
		char dest[50];
		cin >> src >> dest;
		cin >> NumOutputImages >> NumClicks;
        source_image = imread(src);
		destination_image = imread(dest);
		source.readImage(src) ;
		det.readImage(dest) ;
		namedWindow("Source Image");
		namedWindow("Destination Image");

		setMouseCallback("Source Image", on_mouse, &si);
		setMouseCallback("Destination Image", on_mouse, &di);

		imshow("Source Image", source_image);
		imshow("Destination Image", destination_image);
		waitKey(0);

		machine.Initialize(NumClicks+4,source.CoOrds) ;
        machine.PerformTriangulation() ;
        for(long i=1 ; i<=NumOutputImages ; i++){
                	CreateIntermediateImage(i) ;
                    string s = "Image" ;
                	stringstream ss ;
                	ss << i ;
                	string s1 = ss.str() ;
                	s+=s1 ;
                	s1 = ".ppm" ;
                	s+=s1 ;
                	char filename[s.size()+1];
                	strcpy(filename,s.c_str());
                	iges[i].writeImage(filename) ;
        }
        /*Image VideoWriter ;
        Image Video[2+NumOutputImages] ;
        Video[0]=source ;
        Video[NumOutputImages+1]=det ;
        for(long i=1 ; i<=NumOutputImages ; i++){
        	Video[i]=iges[i] ;
        }
        VideoWriter.writeVideo(Video,NumOutputImages+2,"Morphing.avi") ; */

}
